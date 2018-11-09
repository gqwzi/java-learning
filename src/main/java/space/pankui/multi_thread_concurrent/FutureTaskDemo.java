package space.pankui.multi_thread_concurrent;

import java.util.concurrent.*;

/**
 * @author pankui
 * @date 2018/11/1
 * <pre>
 *      一些很耗时的操作，可以用Future转化成异步，不阻塞后续的处理，直到真正需要返回结果时调用get拿到结果
 * </pre>
 */
public class FutureTaskDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        Callable<String> callable = () -> {
            System.out.println("很耗时的操作处理中。。。");
            Thread.sleep(5000);
            return "done";
        };

        FutureTask<String> futureTask = new FutureTask<>(callable);

        System.out.println("就绪。。。");
        new Thread(futureTask).start();
        System.out.println("主线程其它处理。。。");
        System.out.println(futureTask.get());
        System.out.println("处理完成！");

        System.out.println("-----------------");

        System.out.println("executor 就绪。。。");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(callable);
        System.out.println(future.get(3, TimeUnit.SECONDS));
    }
}
