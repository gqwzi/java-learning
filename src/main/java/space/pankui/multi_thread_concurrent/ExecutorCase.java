package space.pankui.multi_thread_concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author pankui
 * @date 2018/7/22
 * <pre>
 *
 * </pre>
 */
public class ExecutorCase {

    private static Executor executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            executor.execute(new Task());
        }
    }

    static class Task implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());
        }
    }
}
