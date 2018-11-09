package space.pankui.multi_thread_concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author pankui
 * @date 2018/9/27
 * <pre>
 *  http://blog.zhangjikai.com/2017/04/15/%E3%80%90Java-%E5%B9%B6%E5%8F%91%E3%80%91%E8%AF%A6%E8%A7%A3-AbstractQueuedSynchronizer/
 *
 *  自定义组件通过使用同步器提供的模板方法来实现自己的同步语义。
 *  下面我们通过两个示例，看下如何借助于 AQS 来实现锁的同步语义。
 *  我们首先实现一个独占锁（排它锁），独占锁就是说在某个时刻内，只能有一个线程持有独占锁，
 *  只有持有锁的线程释放了独占锁，其他线程才可以获取独占锁。下面是具体实现
 *
 *
 *  自定义独占锁
 *
 * </pre>
 */
public class Mutex implements Lock {

    //通过继承AQS，自定义同步器
    private static class Sync extends AbstractQueuedSynchronizer {

        //当前线程是否被独占

        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        //尝试获得锁

        @Override
        protected boolean tryAcquire(int arg) {
            //只有当state 的值未 0,并且线程成功将state 值修改为1 之后，线程才可以获取独占锁
            if (compareAndSetState(0,1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }

            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            //state 为0 说明当前同步块中没有锁了，无需释放
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            //将独占锁的线程设置为null
            setExclusiveOwnerThread(null);
            //将状态变量的值设置为0,以便其他线程可以成功修改状态变量从而获得锁
            setState(0);
            return true;
        }

        Condition newCondition() {
            return new ConditionObject();
        }
    }

    //将操作代理到Sync 上
    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    public boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    public boolean isLocked() {
        return sync.isHeldExclusively();
    }

    public static void withoutMutex() throws InterruptedException {
        System.out.println("without mutext:");
        int threadCount = 2;
        final Thread thread[] = new Thread[threadCount];
        for (int i = 0; i < thread.length; i++) {
            final int index = i;
            thread[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10000; j++) {
                        if (j % 2000 == 0) {
                            System.out.println("Thread-" + index + ": j = " + j);
                        }
                    }
                }
            });
        }

        for (int i = 0; i < thread.length; i++) {
            thread[i].start();
        }

        for (int j = 0; j < thread.length; j++) {
            thread[j].join();
        }
    }


    public static void withMutex() {
        System.out.println("With mutex: ");
        final Mutex mutex = new Mutex();
        int threadCount = 2;
        final Thread threads[] = new Thread[threadCount];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    mutex.lock();
                    try {
                        for (int j = 0; j < 100000; j++) {
                            if (j % 20000 == 0) {
                                System.out.println("Thread-" + index + ": j =" + j);
                            }
                        }
                    } finally {
                        mutex.unlock();
                    }
                }
            });
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        withoutMutex();
        System.out.println();
        withMutex();
    }

    /**
     程序的运行结果如下面所示。我们看到使用了 Mutex 之后，
     线程 0 和线程 1 不会再交替执行，而是当一个线程执行完，另外一个线程再执行。
     */

}
