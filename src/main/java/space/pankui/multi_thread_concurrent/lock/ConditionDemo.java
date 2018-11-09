package space.pankui.multi_thread_concurrent.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author pankui
 * @date 2018/8/16
 * <pre>
 *
 * </pre>
 */
public class ConditionDemo {

    public static void main(String[] args) {
        ConditionDemo conditionDemo = new ConditionDemo();

        conditionDemo.test();
    }
    public void test() {
        final ReentrantLock reentrantLock = new ReentrantLock();
        final Condition condition = reentrantLock.newCondition();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + "开始执行...");
                    reentrantLock.lock();
                    System.out.println(Thread.currentThread().getName() + "我要等一个新信号");
                    // 这里等待...所以线程2 获取锁.
                    condition.await();
                    System.out.println(Thread.currentThread().getName() + "拿到一个信号！！");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    reentrantLock.unlock();
                }
            }
        }, "thread1").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + "开始执行...");
                    reentrantLock.lock();
                    System.out.println(Thread.currentThread().getName() + "抢到了锁");
                    condition.signal();
                    System.out.println(Thread.currentThread().getName() + "我发了一个信号！");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    reentrantLock.unlock();
                }
            }
        }, "thread2").start();
    }
}
