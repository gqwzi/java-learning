package space.pankui.multi_thread_concurrent;

import java.util.concurrent.*;

/**
 * @author pankui
 * @date 2018/7/22
 * <pre>
 *
 * </pre>
 */
public class ExecutorCaseDemo2 {

    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        Future<String> future = executor.submit(new Task());
        System.out.println("do other things ");
        try {
            String result = future.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    static class Task implements Callable<String> {


        @Override
        public String call() throws Exception {
            try {
                TimeUnit.SECONDS.sleep(2);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "this is future case ";
        }
    }
}
