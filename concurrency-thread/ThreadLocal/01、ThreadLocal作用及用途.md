
## [原文](https://www.jianshu.com/p/bb3cab804ec0)

# ThreadLocal作用及用途

ThreadLocal的作用是提供线程内的局部变量，在多线程环境下访问时能保证各个线程内的ThreadLocal变量各自独立。

也就是说，每个线程的ThreadLocal变量是自己专用的，其他线程是访问不到的。

ThreadLocal最常用于以下这个场景：多线程环境下存在对非线程安全对象的并发访问，
而且该对象不需要在线程间共享，但是我们不想加锁，
这时候可以使用ThreadLocal来使得每个线程都持有一个该对象的副本。

例子见: [ThreadLocalDemo.java](/src/main/java/space/pankui/basic/ThreadLocalDemo.java)

 