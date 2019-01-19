
## [原文](https://www.jianshu.com/p/3ea4a6b57f87)

## [原文](http://www.fanyilun.me/2015/10/29/Java%E5%8F%8D%E5%B0%84%E5%8E%9F%E7%90%86/)

# 深入分析Java方法反射的实现原理

“物有本末，事有始终。知其先后，则近道矣”

前段时间看了笨神的 [从一起GC血案谈到反射原理一本](https://link.jianshu.com/?t=http://mp.weixin.qq.com/s/5H6UHcP6kvR2X5hTj_SBjA)，
就把Java方法的反射机制实现撸了一遍。

## 方法反射实例
```java

public class ReflectCase {

    public static void main(String[] args) throws Exception {
        Proxy target = new Proxy();
        Method method = Proxy.class.getDeclaredMethod("run");
        method.invoke(target);
    }

    static class Proxy {
        public void run() {
            System.out.println("run");
        }
    }
}

```

通过Java的反射机制，可以在运行期间调用对象的任何方法，如果大量使用这种方式进行调用，
会有性能或内存隐患么？为了彻底了解方法的反射机制，只能从底层代码入手了。

## Method获取

调用Class类的getDeclaredMethod可以获取指定方法名和参数的方法对象Method。

- getDeclaredMethod
```java
    @CallerSensitive
    public Method[] getDeclaredMethods() throws SecurityException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkMemberAccess(sm, Member.DECLARED, Reflection.getCallerClass(), true);
        }
        // privateGetDeclaredMethods方法从缓存或JVM中获取该Class中申明的方法列
        return copyMethods(privateGetDeclaredMethods(false));
    }
```
 
其中privateGetDeclaredMethods方法从缓存或JVM中获取该Class中申明的方法列表，
searchMethods方法将从返回的方法列表里找到一个匹配名称和参数的方法对象。

- searchMethods
```java
    // This method does not copy the returned Method object!
    // 此方法不会复制返回的Method对象！
    private static Method searchMethods(Method[] methods,
                                        String name,
                                        Class<?>[] parameterTypes)
    {
        ReflectionFactory fact = getReflectionFactory();
        Method res = null;
        for (Method m : methods) {
            if (m.getName().equals(name)
                && arrayContentsEq(parameterTypes,
                                   fact.getExecutableSharedParameterTypes(m))
                && (res == null
                    || (res.getReturnType() != m.getReturnType()
                        && res.getReturnType().isAssignableFrom(m.getReturnType()))))
                res = m;
        }
        return res;
    }

```
[在jdk 11源码返回不copy method]
~~如果找到一个匹配的Method，则重新copy一份返回，即Method.copy()方法~~
 
- privateGetDeclaredMethods
从缓存或JVM中获取该Class中申明的方法列表，实现如下：

```java
    // Returns an array of "root" methods. These Method objects must NOT
    // be propagated to the outside world, but must instead be copied
    // via ReflectionFactory.copyMethod.
    private Method[] privateGetDeclaredMethods(boolean publicOnly) {
        Method[] res;
        ReflectionData<T> rd = reflectionData();
        if (rd != null) {
            res = publicOnly ? rd.declaredPublicMethods : rd.declaredMethods;
            if (res != null) return res;
        }
        // No cached value available; request value from VM
        res = Reflection.filterMethods(this, getDeclaredMethods0(publicOnly));
        if (rd != null) {
            if (publicOnly) {
                rd.declaredPublicMethods = res;
            } else {
                rd.declaredMethods = res;
            }
        }
        return res;
    }
```
 
其中reflectionData()方法实现如下：
```java
    // Lazily create and cache ReflectionData
    private ReflectionData<T> reflectionData() {
        SoftReference<ReflectionData<T>> reflectionData = this.reflectionData;
        int classRedefinedCount = this.classRedefinedCount;
        ReflectionData<T> rd;
        if (reflectionData != null &&
            (rd = reflectionData.get()) != null &&
            rd.redefinedCount == classRedefinedCount) {
            return rd;
        }
        // else no SoftReference or cleared SoftReference or stale ReflectionData
        // -> create and replace new instance
        return newReflectionData(reflectionData, classRedefinedCount);
    }
```
 
这里有个比较重要的数据结构ReflectionData，用来缓存从JVM中读取类的如下属性数据：
```java
    // Reflection data caches various derived names and reflective members. Cached
    // values may be invalidated when JVM TI RedefineClasses() is called
    private static class ReflectionData<T> {
        volatile Field[] declaredFields;
        volatile Field[] publicFields;
        volatile Method[] declaredMethods;
        volatile Method[] publicMethods;
        volatile Constructor<T>[] declaredConstructors;
        volatile Constructor<T>[] publicConstructors;
        // Intermediate results for getFields and getMethods
        volatile Field[] declaredPublicFields;
        volatile Method[] declaredPublicMethods;
        volatile Class<?>[] interfaces;

        // Cached names
        String simpleName;
        String canonicalName;
        static final String NULL_SENTINEL = new String();

        // Value of classRedefinedCount when we created this ReflectionData instance
        final int redefinedCount;

        ReflectionData(int redefinedCount) {
            this.redefinedCount = redefinedCount;
        }
    }
``` 

从reflectionData()方法实现可以看出：reflectionData对象是SoftReference类型的，
说明在内存紧张时可能会被回收，不过也可以通过-XX:SoftRefLRUPolicyMSPerMB参数控制回收的时机，
只要发生GC就会将其回收，如果reflectionData被回收之后，又执行了反射方法，
那只能通过newReflectionData方法重新创建一个这样的对象了，newReflectionData方法实现如下：
```java

    private ReflectionData<T> newReflectionData(SoftReference<ReflectionData<T>> oldReflectionData,
                                                int classRedefinedCount) {
        while (true) {
            ReflectionData<T> rd = new ReflectionData<>(classRedefinedCount);
            // try to CAS it...
            if (Atomic.casReflectionData(this, oldReflectionData, new SoftReference<>(rd))) {
                return rd;
            }
            // else retry
            oldReflectionData = this.reflectionData;
            classRedefinedCount = this.classRedefinedCount;
            if (oldReflectionData != null &&
                (rd = oldReflectionData.get()) != null &&
                rd.redefinedCount == classRedefinedCount) {
                return rd;
            }
        }
    }
```
 
通过unsafe.compareAndSwapObject方法重新设置reflectionData字段；

在privateGetDeclaredMethods方法中，如果通过reflectionData()获得的ReflectionData对象不为空，
则尝试从ReflectionData对象中获取declaredMethods属性，如果是第一次，或则被GC回收之后，
重新初始化后的类属性为空，则需要重新到JVM中获取一次，并赋值给ReflectionData，
下次调用就可以使用缓存数据了。

## Method调用
获取到指定的方法对象Method之后，就可以调用它的invoke方法了，invoke实现如下：

```java
    @CallerSensitive
    @ForceInline // to ensure Reflection.getCallerClass optimization
    @HotSpotIntrinsicCandidate
    public Object invoke(Object obj, Object... args)
        throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException
    {
        if (!override) {
            Class<?> caller = Reflection.getCallerClass();
            checkAccess(caller, clazz,
                        Modifier.isStatic(modifiers) ? null : obj.getClass(),
                        modifiers);
        }
        MethodAccessor ma = methodAccessor;             // read volatile
        if (ma == null) {
            ma = acquireMethodAccessor();
        }
        return ma.invoke(obj, args);
    }
```
 
应该注意到：这里的MethodAccessor对象是invoke方法实现的关键，
一开始methodAccessor为空，需要调用acquireMethodAccessor生成一个新的MethodAccessor对象，
MethodAccessor本身就是一个接口，实现如下：

```java
public interface MethodAccessor {
    /** Matches specification in {@link java.lang.reflect.Method} */
    public Object invoke(Object obj, Object[] args)
        throws IllegalArgumentException, InvocationTargetException;
}
```

 
在acquireMethodAccessor方法中，
会通过ReflectionFactory类的newMethodAccessor创建一个实现了MethodAccessor接口的对象，实现如下：

```java

    public MethodAccessor newMethodAccessor(Method method) {
        checkInitted();

        if (Reflection.isCallerSensitive(method)) {
            Method altMethod = findMethodForReflection(method);
            if (altMethod != null) {
                method = altMethod;
            }
        }

        // use the root Method that will not cache caller class
        Method root = langReflectAccess.getRoot(method);
        if (root != null) {
            method = root;
        }

        if (noInflation && !ReflectUtil.isVMAnonymousClass(method.getDeclaringClass())) {
            return new MethodAccessorGenerator().
                generateMethod(method.getDeclaringClass(),
                               method.getName(),
                               method.getParameterTypes(),
                               method.getReturnType(),
                               method.getExceptionTypes(),
                               method.getModifiers());
        } else {
            NativeMethodAccessorImpl acc =
                new NativeMethodAccessorImpl(method);
            DelegatingMethodAccessorImpl res =
                new DelegatingMethodAccessorImpl(acc);
            acc.setParent(res);
            return res;
        }
    }
```

 
在ReflectionFactory类中，有2个重要的字段：noInflation(默认false)和inflationThreshold(默认15)，
在checkInitted方法中可以通过-Dsun.reflect.inflationThreshold=xxx和
-Dsun.reflect.noInflation=true对这两个字段重新设置，而且只会设置一次；

如果noInflation为false，方法newMethodAccessor都会返回DelegatingMethodAccessorImpl对象，
DelegatingMethodAccessorImpl的类实现
```java
class DelegatingMethodAccessorImpl extends MethodAccessorImpl {
    private MethodAccessorImpl delegate;

    DelegatingMethodAccessorImpl(MethodAccessorImpl delegate) {
        setDelegate(delegate);
    }

    public Object invoke(Object obj, Object[] args)
        throws IllegalArgumentException, InvocationTargetException
    {
        return delegate.invoke(obj, args);
    }

    void setDelegate(MethodAccessorImpl delegate) {
        this.delegate = delegate;
    }
}

```

 
其实，DelegatingMethodAccessorImpl对象就是一个代理对象，
负责调用被代理对象delegate的invoke方法，其中delegate参数目前是NativeMethodAccessorImpl对象，
所以最终Method的invoke方法调用的是NativeMethodAccessorImpl对象invoke方法，实现如下：

```java

/** Used only for the first few invocations of a Method; afterward,
    switches to bytecode-based implementation */

class NativeMethodAccessorImpl extends MethodAccessorImpl {
    private final Method method;
    private DelegatingMethodAccessorImpl parent;
    private int numInvocations;

    NativeMethodAccessorImpl(Method method) {
        this.method = method;
    }

    public Object invoke(Object obj, Object[] args)
        throws IllegalArgumentException, InvocationTargetException
    {
        // We can't inflate methods belonging to vm-anonymous classes because
        // that kind of class can't be referred to by name, hence can't be
        // found from the generated bytecode.
        if (++numInvocations > ReflectionFactory.inflationThreshold()
                && !ReflectUtil.isVMAnonymousClass(method.getDeclaringClass())) {
            MethodAccessorImpl acc = (MethodAccessorImpl)
                new MethodAccessorGenerator().
                    generateMethod(method.getDeclaringClass(),
                                   method.getName(),
                                   method.getParameterTypes(),
                                   method.getReturnType(),
                                   method.getExceptionTypes(),
                                   method.getModifiers());
            parent.setDelegate(acc);
        }

        return invoke0(method, obj, args);
    }

    void setParent(DelegatingMethodAccessorImpl parent) {
        this.parent = parent;
    }

    private static native Object invoke0(Method m, Object obj, Object[] args);
}

```





这里用到了ReflectionFactory类中的inflationThreshold，当delegate调用了15次invoke方法之后，
如果继续调用就通过MethodAccessorGenerator类的generateMethod方法生成MethodAccessorImpl对象，
并设置为delegate对象，这样下次执行Method.invoke时，就调用新建的MethodAccessor对象的invoke()方法了。

这里需要注意的是：

generateMethod方法在生成MethodAccessorImpl对象时，会在内存中生成对应的字节码，
并调用ClassDefiner.defineClass创建对应的class对象，实现如下：

// MethodAccessorGenerator 类
```java
  // matter.
        return AccessController.doPrivileged(
            new PrivilegedAction<MagicAccessorImpl>() {
                @SuppressWarnings("deprecation") // Class.newInstance
                public MagicAccessorImpl run() {
                        try {
                        return (MagicAccessorImpl)
                        ClassDefiner.defineClass
                                (generatedName,
                                 bytes,
                                 0,
                                 bytes.length,
                                 declaringClass.getClassLoader()).newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new InternalError(e);
                        }
                    }
                });
    }
```





在ClassDefiner.defineClass方法实现中，每被调用一次都会生成一个DelegatingClassLoader类加载器对象


```java
    static Class<?> defineClass(String name, byte[] bytes, int off, int len,
                                final ClassLoader parentClassLoader)
    {
        ClassLoader newLoader = AccessController.doPrivileged(
            new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                        return new DelegatingClassLoader(parentClassLoader);
                    }
                });
        return unsafe.defineClass(name, bytes, off, len, newLoader, null);
    }
```




这里每次都生成新的类加载器，是为了性能考虑，在某些情况下可以卸载这些生成的类，
因为类的卸载是只有在类加载器可以被回收的情况下才会被回收的，如果用了原来的类加载器，
那可能导致这些新创建的类一直无法被卸载，从其设计来看本身就不希望这些类一直存在内存里的，在需要的时候有就行了。

 