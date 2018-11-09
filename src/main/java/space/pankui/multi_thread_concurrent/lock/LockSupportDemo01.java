package space.pankui.multi_thread_concurrent.lock;

/**
 * @author pankui
 * @date 2018/9/20
 * <pre>
 *
 * </pre>
 */
public class LockSupportDemo01 {


    public static void main(String[] args) {
        TreadLockSupport treadLockSupport = new TreadLockSupport("TreadLockSupport");

        synchronized (treadLockSupport) {
            try {
                System.out.println(Thread.currentThread().getName() + " start ta");
                treadLockSupport.start();

                System.out.println(Thread.currentThread().getName() + " block");
                // 主线程等待
                treadLockSupport.wait();

                System.out.println(Thread.currentThread().getName() + " continue");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    static class TreadLockSupport extends Thread {

        public TreadLockSupport(String name) {
            super(name);
        }

        @Override
        public void run() {
            // 通过synchronized(this) 获取 当前对象的同步锁
            synchronized (this) {
                System.out.println(Thread.currentThread().getName() + " warkup others");

                //唤醒 当前对象上的等待线程
                notify();
            }
        }
    }
}
