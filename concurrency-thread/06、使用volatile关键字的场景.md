
## [原文](https://www.cnblogs.com/dolphin0520/p/3920373.html)


# 五.使用volatile关键字的场景


synchronized关键字是防止多个线程同时执行一段代码，
那么就会很影响程序执行效率，
而volatile关键字在某些情况下性能要优于synchronized，
但是要注意volatile关键字是无法替代synchronized关键字的，
因为volatile关键字无法保证操作的原子性。
通常来说，使用volatile必须具备以下2个条件：

　　1）对变量的写操作不依赖于当前值

　　2）该变量没有包含在具有其他变量的不变式中

　　实际上，这些条件表明，可以被写入 volatile 变量的这些有效值独立于任何程序的状态，包括变量的当前状态。

　　事实上，我的理解就是上面的2个条件需要保证操作是原子性操作，
才能保证使用volatile关键字的程序在并发时能够正确执行。

　　下面列举几个Java中使用volatile的几个场景。

1.状态标记量

```java
volatile boolean flag = false;
 
while(!flag){
    doSomething();
}
 
public void setFlag() {
    flag = true;
}
 
```
```java
volatile boolean inited = false;
//线程1:
context = loadContext();  
inited = true;            
 
//线程2:
while(!inited ){
sleep()
}
doSomethingwithconfig(context);
 
```


2.double check

```java
class Singleton{
    private volatile static Singleton instance = null;
     
    private Singleton() {
         
    }
     
    public static Singleton getInstance() {
        if(instance==null) {
            synchronized (Singleton.class) {
                if(instance==null)
                    instance = new Singleton();
            }
        }
        return instance;
    }
}
```

 　　至于为何需要这么写请参考：

　　《Java 中的双重检查（Double-Check）》<http://blog.csdn.net/dl88250/article/details/5439024>

　　和<http://www.iteye.com/topic/652440>

　　参考资料：

　　《Java编程思想》

　　《深入理解Java虚拟机》

　　<http://jiangzhengjun.iteye.com/blog/652532>

   <http://blog.sina.com.cn/s/blog_7bee8dd50101fu8n.html>

　　<http://ifeve.com/volatile/>

　　<http://blog.csdn.net/ccit0519/article/details/11241403>

　　<http://blog.csdn.net/ns_code/article/details/17101369>

　　<http://www.cnblogs.com/kevinwu/archive/2012/05/02/2479464.html>

　　<http://www.cppblog.com/elva/archive/2011/01/21/139019.html>

　　<http://ifeve.com/volatile-array-visiblity/>

　　<http://www.bdqn.cn/news/201312/12579.shtml>

　　<http://exploer.blog.51cto.com/7123589/1193399>

　　<http://www.cnblogs.com/Mainz/p/3556430.html>