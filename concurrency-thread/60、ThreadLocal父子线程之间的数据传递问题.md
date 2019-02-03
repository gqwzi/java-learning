
## [原文](https://my.oschina.net/pwh19920920/blog/2989103)


# ThreadLocal父子线程之间的数据传递问题


## 一、问题的提出
在系统开发过程中常使用ThreadLocal进行传递日志的RequestId，由此来获取整条请求链路。
然而当线程中开启了其他的线程，此时ThreadLocal里面的数据将会出现无法获取／读取错乱，
甚至还可能会存在内存泄漏等问题，下面用代码来演示一下这个问题。

普通代码示例：
```java
    @Test
    public void testThreadLocal1() {

        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        threadLocal.set("我是主线程");
        Thread thread = new Thread(() ->
                System.out.println(threadLocal.get())
        );
        thread.start();
    }
```
输出结果
```
null
```

并行流代码示例：
```java
    @Test
    public void testThreadLocal2() {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        threadLocal.set("我是主线程");
        // 并行流代码示例：
        IntStream.range(1, 10).parallel().forEach(id ->
                System.out.println(id + "-" + threadLocal.get())
        );
    }
```
输出结果
```
6-我是主线程
5-我是主线程
7-null
8-null
4-我是主线程
1-null
9-null
3-null
2-null
```


## 二、问题的解决
ThreadLocal的子类InheritableThreadLocal其实已经帮我们处理好了，
通过这个组件可以实现父子线程之间的数据传递，在子线程中能够父线程中的ThreadLocal本地变量。

三、源码的分析
```java
package java.lang;
import java.lang.ref.*;

/**
 * This class extends <tt>ThreadLocal</tt> to provide inheritance of values
 * from parent thread to child thread: when a child thread is created, the
 * child receives initial values for all inheritable thread-local variables
 * for which the parent has values.  Normally the child's values will be
 * identical to the parent's; however, the child's value can be made an
 * arbitrary function of the parent's by overriding the <tt>childValue</tt>
 * method in this class.
 *
 * <p>Inheritable thread-local variables are used in preference to
 * ordinary thread-local variables when the per-thread-attribute being
 * maintained in the variable (e.g., User ID, Transaction ID) must be
 * automatically transmitted to any child threads that are created.
 *
 * @author  Josh Bloch and Doug Lea
 * @see     ThreadLocal
 * @since   1.2
 */

public class InheritableThreadLocal<T> extends ThreadLocal<T> {
    /**
     * Computes the child's initial value for this inheritable thread-local
     * variable as a function of the parent's value at the time the child
     * thread is created.  This method is called from within the parent
     * thread before the child is started.
     * <p>
     * This method merely returns its input argument, and should be overridden
     * if a different behavior is desired.
     *
     * @param parentValue the parent thread's value
     * @return the child thread's initial value
     */
    protected T childValue(T parentValue) {
        return parentValue;
    }

    /**
     * Get the map associated with a ThreadLocal.
     *
     * @param t the current thread
     */
    ThreadLocalMap getMap(Thread t) {
       return t.inheritableThreadLocals;
    }

    /**
     * Create the map associated with a ThreadLocal.
     *
     * @param t the current thread
     * @param firstValue value for the initial entry of the table.
     */
    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }
}

```


可以看出InheritableThreadLocal继承自ThreadLocal，并重写了三个相关方法。

再回来过来看ThreadLocal的源码：
```java

    /**
     * Get the map associated with a ThreadLocal. Overridden in
     * InheritableThreadLocal.
     *
     * @param  t the current thread
     * @return the map
     */
    ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }

    /**
     * Create the map associated with a ThreadLocal. Overridden in
     * InheritableThreadLocal.
     *
     * @param t the current thread
     * @param firstValue value for the initial entry of the map
     */
    void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }

```


我们发现InheritableThreadLocal中createMap，以及getMap方法处理的对象不一样了，
其中在ThreadLocal中处理的是threadLocals，而InheritableThreadLocal中的是inheritableThreadLocals，
我们再顺藤摸瓜看一下Thread对象的处理，其中在init源码中我们看到这么一段代码：
```java
 /**
     * Initializes a Thread.
     *
     * @param g the Thread group
     * @param target the object whose run() method gets called
     * @param name the name of the new Thread
     * @param stackSize the desired stack size for the new thread, or
     *        zero to indicate that this parameter is to be ignored.
     * @param acc the AccessControlContext to inherit, or
     *            AccessController.getContext() if null
     * @param inheritThreadLocals if {@code true}, inherit initial values for
     *            inheritable thread-locals from the constructing thread
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc,
                      boolean inheritThreadLocals) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        this.name = name;

        Thread parent = currentThread();
        SecurityManager security = System.getSecurityManager();
        if (g == null) {
            /* Determine if it's an applet or not */

            /* If there is a security manager, ask the security manager
               what to do. */
            if (security != null) {
                g = security.getThreadGroup();
            }

            /* If the security doesn't have a strong opinion of the matter
               use the parent thread group. */
            if (g == null) {
                g = parent.getThreadGroup();
            }
        }

        /* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. */
        g.checkAccess();

        /*
         * Do we have the required permissions?
         */
        if (security != null) {
            if (isCCLOverridden(getClass())) {
                security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }

        g.addUnstarted();

        this.group = g;
        this.daemon = parent.isDaemon();
        this.priority = parent.getPriority();
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;
        setPriority(priority);
        if (inheritThreadLocals && parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        /* Stash the specified stack size in case the VM cares */
        this.stackSize = stackSize;

        /* Set thread ID */
        tid = nextThreadID();
    }
```


代码的意思是在Thread获取先父亲线程parent（即要创建子线程的当前这个线程）。
当父亲线程中对inherThreadLocals进行了赋值，就会把当前线程的本地变量（也就是父线程的inherThreadLocals）
进行createInheritedMap方法操作。查看源码createInheritedMap方法，源码可知此操作就是将赋线程的threadLocalMap传递给子线程。
```java

    /**
     * Create the map associated with a ThreadLocal. Overridden in
     * InheritableThreadLocal.
     *
     * @param t the current thread
     * @param firstValue value for the initial entry of the map
     */
    void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }
```


我们写个代码测试一下：
```java
    @Test
    public void testInheritableThreadLocals() {
       ThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();
       inheritableThreadLocal.set("我是主线程，inheritableThreadLocal");
        // 并行流代码示例：
        IntStream.range(1, 10).parallel().forEach(id ->
                System.out.println(id + "-" + inheritableThreadLocal.get())
        );
    }
```

输出
```java
6-我是主线程，inheritableThreadLocal
5-我是主线程，inheritableThreadLocal
7-我是主线程，inheritableThreadLocal
8-我是主线程，inheritableThreadLocal
1-我是主线程，inheritableThreadLocal
4-我是主线程，inheritableThreadLocal
9-我是主线程，inheritableThreadLocal
3-我是主线程，inheritableThreadLocal
2-我是主线程，inheritableThreadLocal
```

看起来似乎真的是解决了我们无法传递的问题。

## 四、真的就这么美好么？我们来和线程池搭配一下
```java
 @Test
    public void testInheritableThreadLocalPool() throws InterruptedException {

        // 如果这里只有一个线程，那么线程2 获取的还是线程1的值
        //原因： 我们的线程池会缓存使用过的线程。当线程需要被重复利用的时候，并不会再重新执行init()初始化方法，
        // 而是直接使用已经创建过的线程，所以这里的值不会二次产生变化
        ExecutorService executors = Executors.newFixedThreadPool(1);
        CountDownLatch countDownLatch1 = new CountDownLatch(1);
        CountDownLatch countDownLatch2 = new CountDownLatch(1);

        ThreadLocal<String> inheritableThreadLocalPool = new InheritableThreadLocal<>();
        inheritableThreadLocalPool.set("我是主线程1，");
        executors.submit(()->{
            System.out.println("线程1："+inheritableThreadLocalPool.get());
            countDownLatch1.countDown();
        });
        countDownLatch1.await();
        System.out.println("线程1 "+inheritableThreadLocalPool.get());
        System.out.println("我是分割线----------------------");
        System.out.println("我是分割线----------------------");

        inheritableThreadLocalPool.set("我是主线程2，");
        executors.submit(()->{
            System.out.println("线程2："+inheritableThreadLocalPool.get());
            countDownLatch2.countDown();
        });
        countDownLatch2.await();
        System.out.println("线程2 "+inheritableThreadLocalPool.get());

        executors.shutdown();
    }
```
```
//只开启一个线程
线程1：我是主线程1，
线程1 我是主线程1，
我是分割线----------------------
我是分割线----------------------
线程2：我是主线程1，
线程2 我是主线程2，
```
测试结果显示两次赋值，得到的结果还是第一次的值！为什么？

其实原因也很简单，我们的线程池会缓存使用过的线程。
当线程需要被重复利用的时候，并不会再重新执行init()初始化方法，
而是直接使用已经创建过的线程，所以这里的值不会二次产生变化，那么该怎么做到真正的父子线程数据传递呢？

## 五、真正的解决方案：阿里的transmittable-thread-local了解一下
JDK的InheritableThreadLocal类可以完成父线程到子线程的值传递。但对于使用线程池等会池化复用线程的组件的情况，
线程由线程池创建好，并且线程是池化起来反复使用的；这时父子线程关系的ThreadLocal值传递已经没有意义，
应用需要的实际上是把任务提交给线程池时的ThreadLocal值传递到任务执行时。

首先分析一下最核心的类：TransmittableThreadLocal



首先TransmittableThreadLocal继承自InheritableThreadLocal，这样可以在不破坏原有InheritableThreadLocal特性的情况下，
还能充分使用Thread线程创建过程中执行init方法，从而达到父子线程传递数据的目的。

这里有一个很重要的变量holder：源码如下

1. holder中存放的是InheritableThreadLocal本地变量。

2. WeakHashMap支持存放空置。



主要的几个相关方法：源码如下

1. get方法调用时，先获取父亲的相关数据判断是否有数据，然后在holder中把自身也给加进去。

2. set方法调用时，先在父亲中设置，再本地判断是holder否为删除或者是新增数据。

3. remove调用时，先删除自身，再删除父亲中的数据，删除也是直接以自身this作为变量Key。



采用包装的形式来处理线程池中的线程不会执行初始化的问题，源码如下：

1. 先取得holder。

2. 备份线程本地数据

3. run原先的方法

4. 还原线程本地数据



备份方法：

1. 先获取holder中的数据

2. 进行迭代，数据在captured中不存在，但是holder中存在，说明是后来加进去的，进行删除。

3. 再将captured设置到当前线程中。



还原方法：

1. 先获取holder中的数据

2. backup中不存在，holder中存在，说明是后面加进去的，进行删除还原操作。

3. 再将backup设置到当前线程中。



## 六、几个典型场景例子。
分布式跟踪系统

日志收集记录系统上下文

应用容器或上层框架跨应用代码给下层SDK传递信息

项目地址：https://github.com/alibaba/transmittable-thread-local
