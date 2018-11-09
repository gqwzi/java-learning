package space.pankui.multi_thread_concurrent.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author pankui
 * @date 2018/8/16
 * <pre>
 *
 * </pre>
 */
public class BoundedBuffer {

    /**
     * 锁对象
     */
    final Lock lock = new ReentrantLock();
    /**
     * 写线程条件
     */
    final Condition notFull = lock.newCondition();
    /**
     * 读线程条件
     */
    final Condition notEmpty = lock.newCondition();

    /**
     * 缓存队列
     */
    final Object[] items = new Object[100];

    /**
     * 写索引
     */
    int putptr;

    /**
     * 读索引
     */
    int takeptr;

    /**
     * 队列中存在的数据个数
     */
    int count;

    public void put(Object x) throws InterruptedException {
        lock.lock();
        try {
            //如果队列满了
            while (count == items.length) {
                notFull.await();//阻塞写线程
            }
            //赋值
            items[putptr] = x;
            //如果写索引写到队列的最后一个位置了，那么置为0
            if (++putptr == items.length) {
                putptr = 0;
            }
            //个数++
            ++count;
            //唤醒读线程
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Object take() throws InterruptedException {
        lock.lock();
        try {
            //如果队列为空
            while (count == 0) {
                //阻塞读线程
                notEmpty.await();
            }
            //取值
            Object x = items[takeptr];
            if (++takeptr == items.length) {
                //如果读索引读到队列的最后一个位置了，那么置为0
                takeptr = 0;
            }
            //个数--
            --count;
            //唤醒写线程
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer boundedBuffer = new BoundedBuffer();

        System.out.println("开始执行..");
        Object o = boundedBuffer.take();

        boundedBuffer.put(o);

        System.out.println("执行结束");
    }

}
