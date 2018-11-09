package space.pankui.multi_thread_concurrent.lock;

/**
 * @author pankui
 * @date 2018/9/18
 * <pre>

 *
 * </pre>
 */
public class LockDemo {

    private boolean isLocked = false;

    public synchronized void lock() {
        while (isLocked) {
            System.out.println("获取锁");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("设置锁");
        isLocked = true;
    }

    public synchronized void unlock() {
        System.out.println("释放锁");
        isLocked = false;
        notifyAll();
    }


    public static void main(String[] args) throws InterruptedException {

        LockDemo lockDemo = new LockDemo();

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("## 调用获取锁");
                    lockDemo.lock();
                }
            }).start();
        }


        Thread.sleep(5000);
        lockDemo.unlock();
    }

    /*
    * ## 执行0
## 执行1
调用获取锁
## 执行2
设置锁
调用获取锁
获取锁
## 执行3
调用获取锁
获取锁
## 执行4
调用获取锁
获取锁
调用获取锁
获取锁
释放锁
设置锁
获取锁
获取锁
获取锁
    *
    * */

}
