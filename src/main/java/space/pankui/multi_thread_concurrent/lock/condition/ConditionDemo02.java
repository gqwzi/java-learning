package space.pankui.multi_thread_concurrent.lock.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author pankui
 * @date 2018/9/25
 * <pre>
 *
 * </pre>
 */
public class ConditionDemo02 {

    private static Lock lock = new ReentrantLock();

    private static Condition condition = lock.newCondition();


    public static void main(String[] args) {

        ThreadA ta = new ThreadA("ta");

        //获取锁
        lock.lock();

        try {
            System.out.println(Thread.currentThread().getName()+" start ta");
            ta.start();

            System.out.println(Thread.currentThread().getName()+" block");
            //等待
            condition.await();

            System.out.println(Thread.currentThread().getName()+" continue");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    static class ThreadA extends Thread {

        public ThreadA(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println("获取锁");
            // 获取锁
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName()+" wakup others");
                //唤醒 condition 所在的锁上的其他线程
                condition.signal();
            }finally {
                // 释放锁
                lock.unlock();

            }
        }
    }
}
