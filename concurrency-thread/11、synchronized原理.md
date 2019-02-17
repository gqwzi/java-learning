

# synchronized原理

synchronized (this)原理：涉及两条指令：monitorenter，monitorexit；

再说同步方法，从同步方法反编译的结果来看，
方法的同步并没有通过指令monitorenter和monitorexit来实现，
相对于普通方法，其常量池中多了ACC_SYNCHRONIZED标示符。

JVM就是根据该标示符来实现方法的同步的：当方法被调用时，
调用指令将会检查方法的 ACC_SYNCHRONIZED 访问标志是否被设置，如果设置了，执行线程将先获取monitor，
获取成功之后才能执行方法体，方法执行完后再释放monitor。
在方法执行期间，其他任何线程都无法再获得同一个monitor对象。 

这个问题会接着追问：java对象头信息，偏向锁，轻量锁，重量级锁及其他们相互间转化。



