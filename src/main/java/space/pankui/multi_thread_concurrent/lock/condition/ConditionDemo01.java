package space.pankui.multi_thread_concurrent.lock.condition;

/**
 * @author pankui
 * @date 2018/9/25
 * <pre>
 *   http://www.cnblogs.com/skywang12345/p/3496716.html
 * </pre>
 */
public class ConditionDemo01 {

    public static void main(String[] args) {

        ThreadA ta = new ThreadA("ta");

        synchronized (ta) {
            System.out.println(ThreadA.currentThread().getName()+" start ta");

            ta.start();

            System.out.println(ThreadA.currentThread().getName()+" block");
            //等待
            try {
                ta.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(ThreadA.currentThread().getName()+" continue");
        }

    }

    static class ThreadA extends Thread {

        public ThreadA(String name) {
            super(name);
        }

        @Override
        public void run() {

            // 通过synchronized(this) 获取当前对象的同步锁
            synchronized (this) {

                System.out.println(Thread.currentThread().getName()+"wakup others");

                notify();
            }
        }
    }
}
