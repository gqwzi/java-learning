package space.pankui.multi_thread_concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * @author pankui
 * @date 2018/10/30
 * <pre>
 *
 * </pre>
 */
public class OomCpuDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        int max = 100;
        for (int i = 0; i < max; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            };
            t.setName("thread-" + i);
            t.start();
        }
        Thread t = new Thread(() -> {
            int i = 0;
            while (true) {
                i = (i++) / 10;
            }
        });
        t.setName("BUSY THREAD");
        t.start();
        System.out.println("ready");
        latch.await();
    }
}
