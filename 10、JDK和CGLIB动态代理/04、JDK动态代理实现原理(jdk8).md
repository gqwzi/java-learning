

## [原文](https://blog.csdn.net/lz710117239/article/details/78658168)

# JDK动态代理实现原理(jdk8)


我们先上动态代理的例子代码，然后进行源码分析：
```java


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
 
public class MyInvocationHandler implements InvocationHandler {
 
    // 目标对象
    private Object target;
 
    /**
     * 构造方法
     * @param target 目标对象
     */
    public MyInvocationHandler(Object target) {
        super();
        this.target = target;
    }
 
 
    /**
     * 执行目标对象的方法
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 
        // 在目标对象的方法执行之前简单的打印一下
        System.out.println("------------------before------------------");
 
        // 执行目标对象的方法
        Object result = method.invoke(target, args);
 
        // 在目标对象的方法执行之后简单的打印一下
        System.out.println("-------------------after------------------");
 
        return result;
    }
 
    /**
     * 获取目标对象的代理对象
     * @return 代理对象
     */
    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                target.getClass().getInterfaces(), this);
    }
}
```

```java
public interface UserService {
 
    /**
     * 目标方法
     */
    public abstract void add();
 
}
```

```java
public class UserServiceImpl implements UserService {
 
    /* (non-Javadoc)
     * @see dynamic.proxy.UserService#add()
     */
    public void add() {
        System.out.println("--------------------add---------------");
    }
}
```

```java
public class ProxyTest {
 
    public static void main(String[] args) {
        // 实例化目标对象
        UserService userService = new UserServiceImpl();
 
        // 实例化InvocationHandler
        MyInvocationHandler invocationHandler = new MyInvocationHandler(userService);
 
        // 根据目标对象生成代理对象
        UserService proxy = (UserService) invocationHandler.getProxy();
 
        // 调用代理对象的方法
        proxy.add();
 
    }
}
```
MyInvocationHandler 类的getProxy()的方法是获取了动态代理的实例，我们从这块代码开始进入源码阅读

getProxy()的方法是获取了动态代理的实例，我们从这块代码开始进入源码阅读
```java
//Proxy
public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
        throws IllegalArgumentException
    {
        Objects.requireNonNull(h);
 
        final Class<?>[] intfs = interfaces.clone();
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
        }
 
        /*
         * Look up or generate the designated proxy class.
         */
        Class<?> cl = getProxyClass0(loader, intfs);
 
        /*
         * Invoke its constructor with the designated invocation handler.
         */
        try {
            if (sm != null) {
                checkNewProxyPermission(Reflection.getCallerClass(), cl);
            }
 
            final Constructor<?> cons = cl.getConstructor(constructorParams);
            final InvocationHandler ih = h;
            if (!Modifier.isPublic(cl.getModifiers())) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                        cons.setAccessible(true);
                        return null;
                    }
                });
            }
            return cons.newInstance(new Object[]{h});

```

先是创建了一个接口的克隆类，并通过getProxyClass0方法动态生成Class类，我们看下getProxyClass0源码：

```java
    private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }
 
        // If the proxy class defined by the given loader implementing
        // the given interfaces exists, this will simply return the cached copy;
        // otherwise, it will create the proxy class via the ProxyClassFactory
        return proxyClassCache.get(loader, interfaces);
    }

```
 
我们要重点看下标红的proxyClassCache.get(loader,interfaces)方法，
java就是通过这个方法生成的动态代理类，proxyClassCache的声明如下：
```java

private static final WeakCache<ClassLoader, Class<?>[], Class<?>>
    proxyClassCache = new WeakCache<>(new KeyFactory(), new ProxyClassFactory());

```
其中的ProxyClassFactory就是生成动态代理的工厂类。

```java    
    public V get(K key, P parameter) {
                。。。
                V value = supplier.get();
                。。。
}

```
这个方法中的supplier.get()方法就是生成了代理类，我们进入看下get()方法，

```java
public synchronized V get() { // serialize access
            // re-check
               。。。
                value = Objects.requireNonNull(valueFactory.apply(key, parameter));
               。。。
}

```
这个同步的方法就是生成代理类的，其中的valueFactory就是我们之前提到了能生成代理类的工厂类，
ProxyClassFactory()，我们进入apply方法：

```java
        public Class<?> apply(ClassLoader loader, Class<?>[] interfaces) {
 
            Map<Class<?>, Boolean> interfaceSet = new IdentityHashMap<>(interfaces.length);
            for (Class<?> intf : interfaces) {
                /*
                 * Verify that the class loader resolves the name of this
                 * interface to the same Class object.
                 */
                Class<?> interfaceClass = null;
                try {
                    interfaceClass = Class.forName(intf.getName(), false, loader);
                } catch (ClassNotFoundException e) {
                }
                if (interfaceClass != intf) {
                    throw new IllegalArgumentException(
                        intf + " is not visible from class loader");
                }
                /*
                 * Verify that the Class object actually represents an
                 * interface.
                 */
                if (!interfaceClass.isInterface()) {
                    throw new IllegalArgumentException(
                        interfaceClass.getName() + " is not an interface");
                }
                /*
                 * Verify that this interface is not a duplicate.
                 */
                if (interfaceSet.put(interfaceClass, Boolean.TRUE) != null) {
                    throw new IllegalArgumentException(
                        "repeated interface: " + interfaceClass.getName());
                }
            }
 
            String proxyPkg = null;     // package to define proxy class in
            int accessFlags = Modifier.PUBLIC | Modifier.FINAL;
 
            /*
             * Record the package of a non-public proxy interface so that the
             * proxy class will be defined in the same package.  Verify that
             * all non-public proxy interfaces are in the same package.
             */
            for (Class<?> intf : interfaces) {
                int flags = intf.getModifiers();
                if (!Modifier.isPublic(flags)) {
                    accessFlags = Modifier.FINAL;
                    String name = intf.getName();
                    int n = name.lastIndexOf('.');
                    String pkg = ((n == -1) ? "" : name.substring(0, n + 1));
                    if (proxyPkg == null) {
                        proxyPkg = pkg;
                    } else if (!pkg.equals(proxyPkg)) {
                        throw new IllegalArgumentException(
                            "non-public interfaces from different packages");
                    }
                }
            }
 
            if (proxyPkg == null) {
                // if no non-public proxy interfaces, use com.sun.proxy package
                proxyPkg = ReflectUtil.PROXY_PACKAGE + ".";
            }
 
            /*
             * Choose a name for the proxy class to generate.
             */
            long num = nextUniqueNumber.getAndIncrement();
            String proxyName = proxyPkg + proxyClassNamePrefix + num;
 
            /*
             * Generate the specified proxy class.
             */
            byte[] proxyClassFile = ProxyGenerator.generateProxyClass(
                proxyName, interfaces, accessFlags);
            try {
                return defineClass0(loader, proxyName,
                                    proxyClassFile, 0, proxyClassFile.length);

```

前面都是一些验证，我们不用管，主要看下后面这几行，
首先是定义代理类的名称proxyName,然后通过PrxoyGenerator.generateProxyClass生成字节码文件（生成字节码文件比较复杂，
我们在此不做过多介绍了）然后通过defineClass0方法去加载这个类，使用的类加载器就是我们之前传入的哪个类，
由此代理类就是生成了，但生成了后是怎么实例化的呢，我们继续分析：

之前我们刚进去的第一个方法，可以向上搜索：newProxyInstance中做了实例化，我们看下：

```java
final Constructor<?> cons = cl.getConstructor(constructorParams);
return cons.newInstance(new Object[]{h});
private static final Class<?>[] constructorParams =
    { InvocationHandler.class };

```
这两个方法，首先，获取了带参数InvocationHandler类的构造器，然后通过构造器，
往里看最后通过调用NativeConstructorAccessorImpl的本地方法实例化了这个类

讲解完了代理类的生成源码，我们一定想要看看代理类的代码是什么样的，下面提供一个生成代理类的方法供大家使用:

```java
/**
 * 代理类的生成工具 
 * @author zyb
 * @since 2012-8-9 
 */
public class ProxyGeneratorUtils {
 
    /**
     * 把代理类的字节码写到硬盘上 
     * @param path 保存路径 
     */
    public static void writeProxyClassToHardDisk(String path) {
        // 第一种方法，这种方式在刚才分析ProxyGenerator时已经知道了  
        // System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", true);  
 
        // 第二种方法  
 
        // 获取代理类的字节码  
        byte[] classFile = ProxyGenerator.generateProxyClass("$Proxy11", UserServiceImpl.class.getInterfaces());
 
        FileOutputStream out = null;
 
        try {
            out = new FileOutputStream(path);
            out.write(classFile);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}  

```

```java
public static void main(String[] args) {
 
        ProxyGeneratorUtils.writeProxyClassToHardDisk("C:/x/$Proxy11.class");

```
此时就会在指定的C盘x文件夹下生成代理类的.class文件，我们看下反编译后的结果：
```java
public final class $Proxy11 extends Proxy implements UserService {
    private static Method m1;
    private static Method m2;
    private static Method m3;
    private static Method m0;
 
    public $Proxy11(InvocationHandler var1) throws  {
        super(var1);
    }
 
    public final boolean equals(Object var1) throws  {
        try {
            return ((Boolean)super.h.invoke(this, m1, new Object[]{var1})).booleanValue();
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }
 
    public final String toString() throws  {
        try {
            return (String)super.h.invoke(this, m2, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }
 
    public final void add() throws  {
        try {
            super.h.invoke(this, m3, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }
 
    public final int hashCode() throws  {
        try {
            return ((Integer)super.h.invoke(this, m0, (Object[])null)).intValue();
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }
 
    static {
        try {
            m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
            m2 = Class.forName("java.lang.Object").getMethod("toString");
            m3 = Class.forName("UserService").getMethod("add");
            m0 = Class.forName("java.lang.Object").getMethod("hashCode");
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(var2.getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(var3.getMessage());
        }
    }
}
```
equals,hashcode,toString都是Object的，先看构造方法，我们前面介绍过通过构造器实例化，
传入的invocationHandler就是我们之前定义的MyInvocationHandler，
所以add()方法会执行MyInvocationHandler的invoke()方法。



## 后话
JDK10（JDK9官方发布一段时间后砍了，但我们可以从JDK源码注释中看到原newProxyInstance已经被修改，getProxyClass0方法被取消了）



```java
// Proxy 类的newProxyInstance 方法 [JDK11]
   @CallerSensitive
    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h) {
    
        // 如果h为空直接抛出空指针异常，之后所有的单纯的判断null并抛异常，都是此方法
        Objects.requireNonNull(h);
        // 获取当前系统安全接口
        final Class<?> caller = System.getSecurityManager() == null
                                    ? null
                                    : Reflection.getCallerClass();

        /*
         * Look up or generate the designated proxy class and its constructor.
         */
        // 查找或生成指定的代理类及其构造函数。
        Constructor<?> cons = getProxyConstructor(caller, loader, interfaces);

        return newProxyInstance(caller, cons, h);
    }

    private static Object newProxyInstance(Class<?> caller, // null if no SecurityManager
                                           Constructor<?> cons,
                                           InvocationHandler h) {
        /*
         * Invoke its constructor with the designated invocation handler.
         */
        try {
            if (caller != null) {
                // 进行包访问权限、类加载器权限等检查
                checkNewProxyPermission(caller, cons.getDeclaringClass());
            }
             // 根据代理类的构造函数对象来创建需要返回的代理类对象
            return cons.newInstance(new Object[]{h});
        } catch (IllegalAccessException | InstantiationException e) {
            throw new InternalError(e.toString(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new InternalError(t.toString(), t);
            }
        }
    }
```
newProxyInstance()方法帮我们执行了查找或生成指定的代理类及其构造函数、生成代理对象；

- 生成代理类、获取构造器: Constructor<?> cons = getProxyConstructor(caller, loader, interfaces);

- 生成代理对象:  return cons.newInstance(new Object[]{h});



通过getProxyClass0方法动态生成Class类，我们看下getProxyConstructor源码

```java
   private static Constructor<?> getProxyConstructor(Class<?> caller,
                                                      ClassLoader loader,
                                                      Class<?>... interfaces)
    {
        // optimization for single interface
        // 优化单一接口
        if (interfaces.length == 1) {
            Class<?> intf = interfaces[0];
            if (caller != null) {
                // loader：接口的类加载器
                // 进行包访问权限、类加载器权限等检查
                checkProxyAccess(caller, loader, intf);
            }
            return proxyCache.sub(intf).computeIfAbsent(
                loader,
                (ld, clv) -> new ProxyBuilder(ld, clv.key()).build()
            );
        } else {
            // interfaces cloned
            // 创建了一个接口的克隆类
            final Class<?>[] intfsArray = interfaces.clone();
            if (caller != null) {
                checkProxyAccess(caller, loader, intfsArray);
            }
            final List<Class<?>> intfs = Arrays.asList(intfsArray);
            return proxyCache.sub(intfs).computeIfAbsent(
                loader,
                (ld, clv) -> new ProxyBuilder(ld, clv.key()).build()
            );
        }
    }
```


