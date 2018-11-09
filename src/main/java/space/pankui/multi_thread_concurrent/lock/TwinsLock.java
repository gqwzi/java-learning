package space.pankui.multi_thread_concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author pankui
 * @date 2018/9/27
 * <pre>
 *  自定义共享锁
 * </pre>
 */
public class TwinsLock implements Lock {
    private static class Sync extends AbstractQueuedSynchronizer {
        public Sync(int resourceCount) {
            if (resourceCount <= 0) {
                throw new IllegalArgumentException("resourceCount must be larger than zero.");
            }
            // 设置可以共享的资源总数
            setState(resourceCount);
        }
        @Override
        protected int tryAcquireShared(int reduceCount) {
            // 使用尝试获得资源，如果成功修改了状态变量（获得了资源）
            // 或者资源的总量小于 0（没有资源了），则返回。
            for (; ; ) {
                int lastCount = getState();
                int newCount = lastCount - reduceCount;
                if (newCount < 0 || compareAndSetState(lastCount, newCount)) {
                    return newCount;
                }
            }
        }
        @Override
        protected boolean tryReleaseShared(int returnCount) {
            // 释放共享资源，因为可能有多个线程同时执行，所以需要使用 CAS 操作来修改资源总数。
            for (; ; ) {
                int lastCount = getState();
                int newCount = lastCount + returnCount;
                if (compareAndSetState(lastCount, newCount)) {
                    return true;
                }
            }
        }
    }
    // 定义两个共享资源，说明同一时间内可以有两个线程同时运行
    private final Sync sync = new Sync(2);
    @Override
    public void lock() {
        sync.acquireShared(1);
    }
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }
    @Override
    public boolean tryLock() {
        return sync.tryAcquireShared(1) >= 0;
    }
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }
    @Override
    public void unlock() {
        sync.releaseShared(1);
    }
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
    public static void main(String[] args) {
        final Lock lock = new TwinsLock();
        int threadCounts = 10;
        Thread threads[] = new Thread[threadCounts];
        for (int i = 0; i < threadCounts; i++) {
            final int index = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        lock.lock();
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            System.out.println(Thread.currentThread().getName());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            lock.unlock();
                        }
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        for (int i = 0; i < threadCounts; i++) {
            threads[i].start();
        }
    }

    /**
     * 运行程序，我们会发现程序每次都会同时打印两条语句，
     * 如下面的形式，证明同时有两个线程在执行。
     *
    * */

}
