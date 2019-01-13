

## [原文](https://www.jianshu.com/p/47382625934d)

# Java Timer 定时任务实现原理及缺陷分析

- Timer是起一个工作线程，然后挨个执行任务队列的中任务。

- ScheduledThreadPoolExecutor可以起若干个工作线程，分别执行任务队列中的各个任务。


## 举个例子[Timer Demo](/src/main/java/space/pankui/source/java/uti/TimerDemo.java)

```java
public class TimerDemo {

    public static void main(String[] args) {
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("task1 run ...  execute time:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("task2 run ...  execute time:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Timer t = new Timer();
        //每1s执行一次
        t.schedule(task1, 0,1000);
        t.schedule(task2, 0,1000);
    }
}
```

我们定义了两个定时任务，每个定时任务的执行周期是1秒钟，每秒执行一次。
task1 执行的时长为2秒钟, task2 执行的时长为1秒钟。

输出结果
```java
task1 run ...  execute time:2019-01-13 21:23:12
task2 run ...  execute time:2019-01-13 21:23:14
task1 run ...  execute time:2019-01-13 21:23:15
task2 run ...  execute time:2019-01-13 21:23:17
task1 run ...  execute time:2019-01-13 21:23:18
task2 run ...  execute time:2019-01-13 21:23:20
```

从结果来看，task1 每次执行的时间间隔为3秒钟，task2每次执行的时间间隔也为3秒钟。
> 从结果可以得出的结论：这个是单线程执行（Timer底层是使用一个单线来实现），
因此前一个任务的延迟或者异常会影响到之后的任务

### 为什么会这样呢？

因为Timer底层是使用一个单线程来实现多个Timer任务处理的，
所有任务都是由同一个线程来调度，所有任务都是串行执行，意味着同一时间只能有一个任务得到执行，
而前一个任务的延迟或者异常会影响到之后的任务。

task1 执行了两秒（休眠2s 验证单线程），然后发现task2也该执行了，
task2执行了一秒，然后再执行task1，这样task1的时间间隔就变成3秒了。



## 原理分析

timer底层是把一个个任务放在一个TaskQueue中，TaskQueue是以平衡二进制堆表示的优先级队列，
他是通过nextExecutionTime进行优先级排序的，距离下次执行时间越短优先级越高，
通过getMin()获得queue[1]，
并且出队的时候通过synchronized保证线程安全，延迟执行和特定时间执行的底层实现类似

## Timer 源码分析

```java
    private void sched(TimerTask task, long time, long period) {
        if (time < 0)
            throw new IllegalArgumentException("Illegal execution time.");

        // Constrain value of period sufficiently to prevent numeric
        // overflow while still being effectively infinitely large.
        if (Math.abs(period) > (Long.MAX_VALUE >> 1))
            period >>= 1;

        synchronized(queue) {
            if (!thread.newTasksMayBeScheduled)
                throw new IllegalStateException("Timer already cancelled.");

            synchronized(task.lock) {
                if (task.state != TimerTask.VIRGIN)
                    throw new IllegalStateException(
                        "Task already scheduled or cancelled");
                task.nextExecutionTime = time;
                task.period = period;
                task.state = TimerTask.SCHEDULED;
            }

            queue.add(task);
            if (queue.getMin() == task)
                queue.notify();
        }
    }
```

```java
public class Timer {
    /**
     * The timer task queue.  This data structure is shared with the timer
     * thread.  The timer produces tasks, via its various schedule calls,
     * and the timer thread consumes, executing timer tasks as appropriate,
     * and removing them from the queue when they're obsolete.
     */
    private final TaskQueue queue = new TaskQueue();

    /**
     * The timer thread.
     */
    private final TimerThread thread = new TimerThread(queue);
    
    //... 其他的省略
    
    }
```

1. Timer 中维护了一个TaskQueue队列，存放TimerTask任务。（TaskQueue 是Timer 内部类）

2. Timer 定义了一个线程，用于执行轮询队列中的Task任务，并执行(TimerThread 是Timer 内部类)。


## 

```java
class TaskQueue {
    /**
     * Priority queue represented as a balanced binary heap: the two children
     * of queue[n] are queue[2*n] and queue[2*n+1].  The priority queue is
     * ordered on the nextExecutionTime field: The TimerTask with the lowest
     * nextExecutionTime is in queue[1] (assuming the queue is nonempty).  For
     * each node n in the heap, and each descendant of n, d,
     * n.nextExecutionTime <= d.nextExecutionTime.
     */
    private TimerTask[] queue = new TimerTask[128];

    //... 其他的省略
}
```
- TaskQueue 内部维护了一个TimerTask数组。

- TimeTask数字中存放了所有的定时任务。

- TimerTask[] 数组是从下标1开始存放元素的。

- 即将要执行的任务永远存放到TimerTask[1] 中，（数组中的任务顺序，是不断的调整的，每次获取完任务后都会调整一次）。

 
### TimerThread.mainLoop() 方法

在Timer中定义了一个内部类 TimerThread，负责执行队列中的任务

我们主要来看下周期性调度通过什么方式实现的，我们直接来分析源码如下：

```java
private void mainLoop() {
  // 首先一直监听队列中有没有任务
        while (true) {
            try {
                TimerTask task;
                boolean taskFired;
    // 同步，保证任务执行顺序
                synchronized(queue) {
                    // Wait for queue to become non-empty
                    while (queue.isEmpty() && newTasksMayBeScheduled)
                        queue.wait();
                    if (queue.isEmpty())
                        break; // Queue is empty and will forever remain; die
 
                    // Queue nonempty; look at first evt and do the right thing
                    long currentTime, executionTime;
     // 获取优先级最高的任务
                    task = queue.getMin();
                    synchronized(task.lock) {
                        if (task.state == TimerTask.CANCELLED) {
                            queue.removeMin();
                            continue; // No action required, poll queue again
                        }
                        currentTime = System.currentTimeMillis();
      // 获取任务下次执行时间
                        executionTime = task.nextExecutionTime;
                        if (taskFired = (executionTime<=currentTime)) {
       // 到这里是延迟执行和特定时间点执行已经结束了，状态标记为EXECUTED,周期性执行继续往下走
                            if (task.period == 0) { // Non-repeating, remove
                                queue.removeMin();
                                task.state = TimerTask.EXECUTED;
                            } else { // Repeating task, reschedule
        // 这里他又重新计算了下下个任务的执行，并且任务还在队列中
                                queue.rescheduleMin(
                                  task.period<0 ? currentTime - task.period
                                                : executionTime + task.period);
                            }
                        }
                    }
     // 如果任务执行时间大于当前时间说明任务还没点，继续等，否则执行run代码块
                    if (!taskFired) // Task hasn't yet fired; wait
                        queue.wait(executionTime - currentTime);
                }
                if (taskFired) // Task fired; run it, holding no locks
                    task.run();
            } catch(InterruptedException e) {
            }
        }
    }
}

```
主要逻辑是：


- 从queue中获取将要执行task1 (TimerTask[1]=task1, TimerTask[2]=task2).

- 获取完成后，然后把queue的TimerTask[1] =task2，TimerTask[2]=task1

- 然后执行获取的task.run()。
 


## 缺陷分析

1、首先Timer对调度的支持是基于绝对时间的，而不是相对时间，所以它对系统时间的改变非常敏感。

2、其次Timer线程是不会捕获异常的，如果TimerTask抛出的了未检查异常则会导致Timer线程终止，
同时Timer也不会重新恢复线程的执行，他会错误的认为整个Timer线程都会取消。
同时，已经被安排单尚未执行的TimerTask也不会再执行了，新的任务也不能被调度。
故如果TimerTask抛出未检查的异常，Timer将会产生无法预料的行为

3、Timer在执行定时任务时只会创建一个线程任务，如果存在多个线程，
若其中某个线程因为某种原因而导致线程任务执行时间过长，超过了两个任务的间隔时间，会导致下一个任务执行时间滞后

这些缺点可以通过ScheduledExecutorService来代替
 
- 异常会导致其他定时任务都结束   
```java
    TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("task1 run ...  execute time:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                try {
                   // 异常会导致所有的定时任务都会停
                   throw new RuntimeException("task1 run ...  execute time RuntimeException");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
```














 