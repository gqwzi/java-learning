package space.pankui.multi_thread_concurrent.lock;


import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author pankui
 * <p>
 * CountDownLatch 很适合用来将一个任务分为n个独立的部分，等这些部分都完成后继续接下来的任务，
 * CountDownLatch 只能出发一次，计数值不能被重置。
 * <p>
 * 基于CountDownLatch 的模拟项目，一个项目可以分为多个模块，只有但这些模块都完成后才可以继续下一步的工作。
 * <p>
 * <p>
 * countDown方法，当前线程调用此方法，则计数减一
 * await方法，调用此方法会一直阻塞当前线程，直到计时器的值为0
 * <p>
 * executorService.shutdown() 并不是终止线程的运行，而是禁止在这个Executor中添加新的任务
 * void shutdown()
 * 启动一个关闭命令，不再接受新任务，当所有已提交任务执行完后，就关闭。如果已经关闭，则调用没有其他作用。
 * 抛出：
 * SecurityException - 如果安全管理器存在并且关闭，此 ExecutorService 可能操作某些不允许调用者修改的线程
 * （因为它没有保持 RuntimePermission("modifyThread")），或者安全管理器的 checkAccess 方法拒绝访问。
 */
public class CountDownLatchDemo03 {
    //定义计数器
    static final int SIZE = 20;

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(SIZE);
        Random random = new Random();
        ExecutorService executorService = Executors.newCachedThreadPool();
        //让等待所有子线程执行完毕
        Controller controller = new Controller(latch);
        executorService.execute(controller);

        //将SIZE个小任务去执行，多个子线程任务
        for (int i = 0; i < SIZE; i++) {
            executorService.execute(new Module(latch, "模块" + (i + 1), random.nextInt(2000)));
        }
        executorService.shutdown();//并不是终止线程的运行，而是禁止在这个Executor中添加新的任务
    }
}


class Module implements Runnable {

    private CountDownLatch latch;

    private String name;

    private int random;

    public Module(CountDownLatch latch, String name, int random) {
        this.latch = latch;
        this.name = name;
        this.random = random;
    }

    @Override
    public void run() {
        work();
        latch.countDown(); //当前线程调用此方法，则计数减一

    }

    private void work() {
        try {
            TimeUnit.MILLISECONDS.sleep(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(name + " 完成，耗时:" + random);
    }
}


class Controller implements Runnable {

    private CountDownLatch latch;

    public Controller(CountDownLatch latch) {
        super();
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            latch.await();  //调用此方法会一直阻塞当前线程，直到计时器的值为0
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("所有任务都完成，任务完成");
    }
}
