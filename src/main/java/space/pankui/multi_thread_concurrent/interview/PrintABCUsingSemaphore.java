package space.pankui.multi_thread_concurrent.interview;

import java.util.concurrent.Semaphore;

/**
 * @author pankui
 * @date 2019-02-12
 * <pre>
 *
 *   三个线程分别打印A，B，C，要求这三个线程一起运行，打印n次，输出形如“ABCABCABC....”的字符串。
 *
 *   Semaphore信号量方式
 *
 *   Semaphore又称信号量，是操作系统中的一个概念，在Java并发编程中，信号量控制的是线程并发的数量。
 *
 *
 *   public Semaphore(int permits)
 *
 *  其中参数permits就是允许同时运行的线程数目;
 *
 *
 * </pre>
 */
public class PrintABCUsingSemaphore {

    private int times;
    // 以A开始的信号量,初始信号量数量为1
    private Semaphore semaphoreA = new Semaphore(1);
    private Semaphore semaphoreB = new Semaphore(0);
    private Semaphore semaphoreC = new Semaphore(0);

    public PrintABCUsingSemaphore(int times) {
        this.times = times;
    }

    public static void main(String[] args) {
        PrintABCUsingSemaphore printABC = new PrintABCUsingSemaphore(10);

        new Thread(printABC::printB).start();
        new Thread(printABC::printA).start();
        new Thread(printABC::printC).start();
    }

    public void printA() {
        try {
            print("A", semaphoreA, semaphoreB);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printB() {
        try {
            print("B", semaphoreB, semaphoreC);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printC() {
        try {
            print("C", semaphoreC, semaphoreA);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void print(String name, Semaphore current, Semaphore next)
            throws InterruptedException {
        for (int i = 0; i < times; i++) {
            // current 获取信号执行,current信号量减1,当current为0时将无法继续获得该信号量
            current.acquire();
            System.out.print(name);
            // next释放信号，信号量加1（初始为0），此时可以获取next信号量
            next.release();
        }
    }
}
