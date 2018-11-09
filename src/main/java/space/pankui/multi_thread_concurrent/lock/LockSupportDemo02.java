package space.pankui.multi_thread_concurrent.lock;

import java.util.concurrent.locks.LockSupport;

/**
 * @author pankui
 * @date 2018/9/25
 * <pre>
 * https://www.cnblogs.com/skywang12345/p/3505784.html
 * </pre>
 */
public class LockSupportDemo02 {


    private static Thread mainThread;

    public static void main(String[] args) {

        ThreadA ta = new ThreadA("ta");

        //获取主线程
        mainThread = Thread.currentThread();

        System.out.println(Thread.currentThread().getName() + " start ta");

        ta.start();

        System.out.println(Thread.currentThread().getName() + " block");

        //主线程阻塞
        LockSupport.park();

        System.out.println(Thread.currentThread().getName() + " continue");


    }

    static class ThreadA extends Thread {

        public ThreadA(String name) {
            super(name);
        }


        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "wakup others");

            //唤醒 "主线程"

            LockSupport.unpark(mainThread);
        }
    }
}
