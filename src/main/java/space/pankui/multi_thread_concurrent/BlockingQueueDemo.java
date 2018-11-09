package space.pankui.multi_thread_concurrent;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author pankui
 * @date 2018/11/1
 *
 * https://www.cnblogs.com/yjmyzz/p/java-concurrent-tools-sample.html
 *
 * <pre>
 *  阻塞队列可以在线程间实现生产者-消费者模式。
 *  比如下面的示例：线程producer模拟快速生产数据，而线程consumer模拟慢速消费数据，
 *  当达到队列的上限时（即：生产者产生的数据，已经放不下了），队列就堵塞住了。
 * </pre>
 */
public class BlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        final BlockingQueue<String> blockingDeque = new ArrayBlockingQueue<>(5);

        Thread producer = new Thread() {
            @Override
            public void run() {
                Random rnd = new Random();
                while (true) {
                    try {
                        int i = rnd.nextInt(10000);
                        blockingDeque.put(i + "");
                        System.out.println(this.getName() + " 产生了一个数字：" + i);
                        Thread.sleep(rnd.nextInt(50));//模拟生产者快速生产
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        producer.setName("producer 1");


        Thread consumer = new Thread() {
            @Override
            public void run() {
                while (true) {
                    Random rnd = new Random();
                    try {

                        String i = blockingDeque.take();
                        System.out.println(this.getName() + " 消费了一个数字：" + i);
                        Thread.sleep(rnd.nextInt(10000));//消费者模拟慢速消费
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        consumer.setName("consumer 1");

        producer.start();
        consumer.start();

        while (true) {
            Thread.sleep(100);
        }
    }
}

/*

producer 1 产生了一个数字：9933
consumer 1 消费了一个数字：9933
producer 1 产生了一个数字：585
producer 1 产生了一个数字：7569
producer 1 产生了一个数字：1633
producer 1 产生了一个数字：4058
producer 1 产生了一个数字：9165
producer 1 产生了一个数字：9895 # 注意这里就已经堵住了，直到有消费者消费一条数据，才能继续生产,
consumer 1 消费了一个数字：585
consumer 1 消费了一个数字：7569
producer 1 产生了一个数字：6469
consumer 1 消费了一个数字：1633
producer 1 产生了一个数字：9683


我只初始化了5个阻塞队列，怎么生产6个来？？ 如果调成4 那么还是4个？？

 */