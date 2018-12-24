
## [原文](https://www.jianshu.com/p/bb3cab804ec0)

# ThreadLocal作用及用途

ThreadLocal的作用是提供线程内的局部变量，在多线程环境下访问时能保证各个线程内的ThreadLocal变量各自独立。

也就是说，每个线程的ThreadLocal变量是自己专用的，其他线程是访问不到的。

ThreadLocal最常用于以下这个场景：多线程环境下存在对非线程安全对象的并发访问，
而且该对象不需要在线程间共享，但是我们不想加锁，
这时候可以使用ThreadLocal来使得每个线程都持有一个该对象的副本。

例子见: [ThreadLocalDemo.java](/src/main/java/space/pankui/basic/ThreadLocalDemo.java)

 
 
 
## [Java中的ThreadLocal通常是在什么情况下使用的？](https://www.zhihu.com/question/21709953)

- PageHelper 的PageInfo

- Hibernate 的 ThreadLocal模式 Session

- Dubbo的RpcConetxt

- 日志的MDC


 

首先要理解ThreadLocal的原理,每个Thread对象内部有个ThreadLocalMap,当线程访问ThreadLocal对象时,
会在线程内部的ThreadLocalMap新建一个Entry,这样的话每个线程都有一个对象的副本,保证了并发场景下的线程安全。
我理解ThreadLocal的使用场景是某些对象在多线程并发访问时可能出现问题,

比如使用SimpleDataFormat的parse()方法，内部有一个Calendar对象，
调用SimpleDataFormat的parse()方法会先调用Calendar.clear(),然后调用Calendar.add()，
如果一个线程先调用了add()然后另一个线程又调用了clear()，这时候parse()方法解析的时间就不对了,
我们就可以用ThreadLocal<SimpleDataFormat>来解决并发修改的问题。

另一种场景是Spring事务,事务是和线程绑定起来的,
Spring框架在事务开始时会给当前线程绑定一个Jdbc Connection,在整个事务过程都是使用该线程绑定的connection来执行数据库操作，
实现了事务的隔离性。
Spring框架里面就是用的ThreadLocal来实现这种隔离，
代码如下所示:
```java

public abstract class TransactionSynchronizationManager {
//线程绑定的资源,比如DataSourceTransactionManager绑定是的某个数据源的一个Connection,在整个事务执行过程中
//都使用同一个Jdbc Connection
private static final ThreadLocal<Map<Object, Object>> resources =
		new NamedThreadLocal<>("Transactional resources");
//事务注册的事务同步器
private static final ThreadLocal<Set<TransactionSynchronization>> synchronizations =
		new NamedThreadLocal<>("Transaction synchronizations");
//事务名称
private static final ThreadLocal<String> currentTransactionName =
		new NamedThreadLocal<>("Current transaction name");
//事务只读属性
private static final ThreadLocal<Boolean> currentTransactionReadOnly =
		new NamedThreadLocal<>("Current transaction read-only status");
//事务隔离级别
private static final ThreadLocal<Integer> currentTransactionIsolationLevel =
		new NamedThreadLocal<>("Current transaction isolation level");
//事务同步开启
private static final ThreadLocal<Boolean> actualTransactionActive =
		new NamedThreadLocal<>("Actual transaction active");
}

```
但是不是说一遇到并发场景就用ThreadLocal来解决,我们还可以用synchronized或者锁来实现线程安全,T
hreadLocal使用不当时会引起内存泄露的问题,比如这个:[Java内存泄露的例子](https://www.ezlippi.com/blog/2017/12/java-memory-leak-example.html)
