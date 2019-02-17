

# synchronized底层原理


查看带有Synchronized语句块的class文件可以看到在同步代码块的起始位置插入了moniterenter指令，
在同步代码块结束的位置插入了monitorexit指令。

(JVM需要保证每一个monitorenter都有一个monitorexit与之相对应，但每个monitorexit不一定都有一个monitorenter)

但是查看同步方法的class文件时，同步方法并没有通过指令monitorenter和monitorexit来完成，
而被翻译成普通的方法调用和返回指令，只是在其常量池中多了ACC_SYNCHRONIZED标示符。
JVM就是根据该标示符来实现方法的同步的：当方法调用时，调用指令将会检查方法的 ACC_SYNCHRONIZED 访问标志是否被设置，
如果设置了，执行线程将先获取monitor，获取成功之后才能执行方法体，方法执行完后再释放monitor。
在方法执行期间，其他任何线程都无法再获得同一个monitor对象。 
其实本质上没有区别，只是方法的同步是一种隐式的方式来实现，无需通过字节码来完成。

moniterenter和moniterexit指令是通过monitor对象实现的。   

Synchronized的实现不仅与monitor对象有关，还与另一个东西密切相关，那就是对象头。

下面我们就来看下Java对象头和monitor对象与Synchronized的实现有着怎样的关系。

JVM规范中对monitorenter和monitorexit指令的描述如下：
```
monitorenter ：
Each object is associated with a monitor. A monitor is locked if and only if it has an owner. 
The thread that executes monitorenter attempts to gain ownership of the monitor associated with objectref, as follows:
• If the entry count of the monitor associated with objectref is zero, 
the thread enters the monitor and sets its entry count to one. The thread is then the owner of the monitor.
• If the thread already owns the monitor associated with objectref, it reenters the monitor, incrementing its entry count.
• If another thread already owns the monitor associated with objectref, 
the thread blocks until the monitor’s entry count is zero, then tries again to gain ownership.
```
这段话的大概意思为： 
```
每个对象都有一个监视器锁(monitor)与之对应。当monitor被占用时就会处于锁定状态，线程执行monitorenter指令时尝试获取monitor的所有权，过程如下：
1、如果monitor的进入数为0，则该线程进入monitor，然后将进入数设置为1，该线程即为monitor的所有者。
2、如果线程已经占有该monitor，只是重新进入，则进入monitor的进入数加1.
3.如果其他线程已经占用了monitor，则该线程进入阻塞状态，直到monitor的进入数为0，再重新尝试获取monitor的所有权。
```
monitorexit：
```　
The thread that executes monitorexit must be the owner of the monitor associated with the instance referenced by objectref.
The thread decrements the entry count of the monitor associated with objectref. 
If as a result the value of the entry count is zero, the thread exits the monitor and is no longer its owner. 
Other threads that are blocking to enter the monitor are allowed to attempt to do so.
```
这段话的大概意思为：
```
执行monitorexit的线程必须是objectref所对应的monitor的所有者。
指令执行时，monitor的进入数减1，如果减1后进入数为0，那线程退出monitor，不再是这个monitor的所有者。
其他被这个monitor阻塞的线程可以尝试去获取这个monitor的所有权。
```
通过这两个指令我们应该能很清楚的看出Synchronized的实现原理，Synchronized的语义底层是通过一个monitor的对象来完成，
其实wait/notify等方法也依赖于monitor对象，这就是为什么只有在同步的块或者方法中才能调用wait/notify等方法，
否则会抛出java.lang.IllegalMonitorStateException的异常的原因。
