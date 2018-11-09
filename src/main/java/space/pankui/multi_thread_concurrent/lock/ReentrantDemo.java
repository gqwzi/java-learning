package space.pankui.multi_thread_concurrent.lock;

/**
 * @author pankui
 * @date 2018/9/18
 * <pre>
 * boolean isLocked = false;
 *   Thread  lockedBy = null;
 *   int     lockedCount = 0;
 *
 *   public synchronized void lock() throws InterruptedException{
 *
 *     Thread callingThread = Thread.currentThread();
 *     //如果是同一个线程调用可重入。
 *     while(isLocked && lockedBy != callingThread){
 *       wait();
 *     }
 *     isLocked = true;
 *     lockedCount++;
 *     lockedBy = callingThread;
 *   }
 *
 *
 *   public synchronized void unlock(){
 *     if(Thread.curentThread() == this.lockedBy){
 *       lockedCount--;
 *
 *       if(lockedCount == 0){
 *         isLocked = false;
 *         notify();
 *       }
 *     }
 *   }
 * </pre>
 */
public class ReentrantDemo {


    boolean isLocked =false;
    Thread lockedBy = null;
    int lockedCount = 0;

    public synchronized void lock (){

        System.out.println("执行获取锁");
        Thread callingThread = Thread.currentThread();

        while (isLocked && lockedBy != callingThread) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.out.println("设置相等"+callingThread);
        isLocked = true;
        lockedBy = callingThread;
        lockedCount ++ ;
    }

    public synchronized void unlock() {
        System.out.println("释放锁");
        if (Thread.currentThread() == this.lockedBy) {
            System.out.println("相等"+this.lockedBy);
            lockedCount --;

            if (lockedCount == 0 ) {
                isLocked =false;
                notify();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantDemo reentrantDemo = new ReentrantDemo();
        reentrantDemo.lock();

        Thread.sleep(5000);

        reentrantDemo.unlock();
    }

}
