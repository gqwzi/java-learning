package space.pankui.multi_thread_concurrent.lock;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author pankui
 * @date 2018/10/10
 * <pre>
 *
 * CyclicBarrier:一个同步辅助类，它允许一组线程互相等待，直到到达某个公共屏障点 (common barrier point)。
 * 	在涉及一组固定大小的线程的程序中，这些线程必须不时地互相等待，此时 CyclicBarrier 很有用。
 * 因为该 barrier 在释放等待线程后可以重用，所以称它为循环 的 barrier。
 * CyclicBarrier可以多次重复使用
 *
 *
 * </pre>
 */
public class CyclicBarrierTest_RelayRace {

    public static void main(String[] args) throws InterruptedException {

        ExecutorService exec = Executors.newCachedThreadPool();

        final CyclicBarrier barrier = new CyclicBarrier(4, new Runnable() {

            @Override
            public void run() {
                System.out.println("好了，大家可以去吃饭了……");
            }
        });

        System.out.println("要吃饭，必须所有人都到终点，oK?");
        System.out.println("不放弃不抛弃！");

        for (int i = 0; i < 4; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + ":Go");
                    try {
                        Thread.sleep((long) (2000 * Math.random()));
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + ":我到终点了");
                    try {
                        // 等待大部队到来！！！！
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    System.out.println(Thread.currentThread().getName() + ":终于可以吃饭啦！");

                }
            });

        }
        exec.shutdown();

    }


}

