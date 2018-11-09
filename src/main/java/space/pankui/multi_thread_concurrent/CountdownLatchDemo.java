package space.pankui.multi_thread_concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * @author pankui
 * @date 2018/11/1
 * <pre>
 *闭锁（门栓）- CountDownLatch
 *
 * 适用场景：多线程测试时，通常为了精确计时，要求所有线程都ready后，才开始执行，
 * 防止有线程先起跑，造成不公平，类似的，所有线程执行完，整个程序才算运行完成。
 * </pre>
 */
public class CountdownLatchDemo {

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch startLatch = new CountDownLatch(1); //类似发令枪
        CountDownLatch endLatch = new CountDownLatch(10);//这里的数量，要与线程数相同

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                try {
                    // 大家可以把第26行注释掉，再看看运行结果有什么不同。
                    // 不去掉 done! exec time 一样，去掉时间就不一样
                    startLatch.await(); //先等着，直到发令枪响，防止有线程先run
                    System.out.println(Thread.currentThread().getName() + " is running...");
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown(); //每个线程执行完成后，计数
                }
            });
            t.setName("线程-" + i);
            t.start();
        }
        long start = System.currentTimeMillis();
        startLatch.countDown();//发令枪响，所有线程『开跑』
        endLatch.await();//等所有线程都完成
        long end = System.currentTimeMillis();
        System.out.println("done! exec time => " + (end - start) + " ms");

    }
}
