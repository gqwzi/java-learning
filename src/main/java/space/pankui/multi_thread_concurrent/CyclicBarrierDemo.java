package space.pankui.multi_thread_concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author pankui
 * @date 2018/11/1
 * <pre>
 *  这里我们假设有一个worder线程，里面有2步操作，要求所有线程完成step1后，才能继续step2.　执行结果如下：
 * </pre>
 */
public class CyclicBarrierDemo extends Thread {

    private CyclicBarrier cyclicBarrier;

    public CyclicBarrierDemo(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(11);
        for (int i = 0; i < 10; i++) {
            CyclicBarrierDemo w = new CyclicBarrierDemo(cyclicBarrier);
            w.start();
        }
        cyclicBarrier.await();

    }

    private void step1() {
        System.out.println(this.getName() + " step 1 wait...");
    }

    private void step2() {
        System.out.println(this.getName() + " step 2 running...");
    }

    @Override
    public void run() {
        step1();
        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        step2();
    }
}
