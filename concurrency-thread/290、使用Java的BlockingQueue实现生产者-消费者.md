

# 使用Java的BlockingQueue实现生产者-消费者
 
BlockingQueue也是java.util.concurrent下的主要用来控制线程同步的工具。

BlockingQueue有四个具体的实现类,根据不同需求,选择不同的实现类
1、ArrayBlockingQueue：一个由数组支持的有界阻塞队列，规定大小的BlockingQueue,
其构造函数必须带一个int参数来指明其大小.其所含的对象是以FIFO(先入先出)顺序排序的。


2、LinkedBlockingQueue：大小不定的BlockingQueue,若其构造函数带一个规定大小的参数,
生成的BlockingQueue有大小限制,若不带大小参数,所生成的BlockingQueue的大小由Integer.MAX_VALUE来决定.
其所含的对象是以FIFO(先入先出)顺序排序的。


3、PriorityBlockingQueue：类似于LinkedBlockQueue,但其所含对象的排序不是FIFO,
而是依据对象的自然排序顺序或者是构造函数的Comparator决定的顺序。


4、SynchronousQueue：特殊的BlockingQueue,对其的操作必须是放和取交替完成的。

LinkedBlockingQueue 可以指定容量，也可以不指定，不指定的话，默认最大是Integer.MAX_VALUE,其中主要用到put和take方法，
put方法在队列满的时候会阻塞直到有队列成员被消费，take方法在队列空的时候会阻塞，直到有队列成员被放进来。

生产者消费者的示例代码：

生产者：

```java
import java.util.concurrent.BlockingQueue;  
  
public class Producer implements Runnable {  
    BlockingQueue<String> queue;  
  
    public Producer(BlockingQueue<String> queue) {  
        this.queue = queue;  
    }  
  
    @Override  
    public void run() {  
        try {  
            String temp = "A Product, 生产线程："  
                    + Thread.currentThread().getName();  
            System.out.println("I have made a product:"  
                    + Thread.currentThread().getName());  
            queue.put(temp);//如果队列是满的话，会阻塞当前线程  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  
  
}  
``` 

 消费者：

```java
import java.util.concurrent.BlockingQueue;  
  
public class Consumer implements Runnable{  
    BlockingQueue<String> queue;  
      
    public Consumer(BlockingQueue<String> queue){  
        this.queue = queue;  
    }  
      
    @Override  
    public void run() {  
        try {  
            String temp = queue.take();//如果队列为空，会阻塞当前线程  
            System.out.println(temp);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  
}
```
  
 测试类：

```java
import java.util.concurrent.ArrayBlockingQueue;  
import java.util.concurrent.BlockingQueue;  
import java.util.concurrent.LinkedBlockingQueue;  
  
public class Test3 {  
  
    public static void main(String[] args) {  
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(2);  
        // BlockingQueue<String> queue = new LinkedBlockingQueue<String>();  
        //不设置的话，LinkedBlockingQueue默认大小为Integer.MAX_VALUE  
          
        // BlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);  
  
        Consumer consumer = new Consumer(queue);  
        Producer producer = new Producer(queue);  
        for (int i = 0; i < 5; i++) {  
            new Thread(producer, "Producer" + (i + 1)).start();  
  
            new Thread(consumer, "Consumer" + (i + 1)).start();  
        }  
    }  
}  
```

 打印结果：
 
```
Text代码  收藏代码
I have made a product:Producer1  
I have made a product:Producer2  
A Product, 生产线程：Producer1  
A Product, 生产线程：Producer2  
I have made a product:Producer3  
A Product, 生产线程：Producer3  
I have made a product:Producer5  
I have made a product:Producer4  
A Product, 生产线程：Producer5  
A Product, 生产线程：Producer4  
```

由于队列的大小限定成了2，所以最多只有两个产品被加入到队列当中，
而且消费者取到产品的顺序也是按照生产的先后顺序，
原因就是LinkedBlockingQueue和ArrayBlockingQueue都是按照FIFO的顺序存取元素的。

<http://tonl.iteye.com/blog/1936391>

<https://blog.csdn.net/tomcat_2014/article/details/60135152>

--- 
