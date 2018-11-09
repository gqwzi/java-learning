package space.pankui.multi_thread_concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author pankui
 * @date 2018/10/22
 * <pre>
 *
 * </pre>
 */
public class ThreadPoolDemo {

    static class WorkThread implements Runnable {

        private String command;

        WorkThread (String command) {
            this.command = command;
        }
        @Override
        public void run() {

            System.out.println("Thread-" + Thread.currentThread().getId() + " start. Command=" + command);
            processCommand();
            System.out.println("Thread-" + Thread.currentThread().getId() + " end.");
        }
        private void processCommand() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public static void main(String[] args) {

            ExecutorService executor = Executors.newFixedThreadPool(5);

            for (int i = 0; i < 10; i++) {
                Runnable work = new WorkThread("" + i);
                executor.execute(work);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            System.out.println("Finish all threads.");
        }
    }
}
