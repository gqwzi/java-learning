package space.pankui.basic;

/**
 * @author pankui
 * @date 2018-12-20
 * <pre>
 *
 *     http://ifeve.com/java-threadlocal%E7%9A%84%E4%BD%BF%E7%94%A8/
 *
 *   下面的例子创建了一个MyRunnable实例，并将该实例作为参数传递给两个线程。
 *
 *   两个线程分别执行run()方法，并且都在ThreadLocal实例上保存了不同的值。
 *   如果它们访问的不是ThreadLocal对象并且调用的set()方法被同步了，则第二个线程会覆盖掉第一个线程设置的值。
 *
 *   但是，由于它们访问的是一个ThreadLocal对象，因此这两个线程都无法看到对方保存的值。
 *   也就是说，它们存取的是两个不同的值
 *
 * </pre>
 */
public class ThreadLocalDemo {

    public static class MyRunnable implements Runnable {

        private ThreadLocal threadLocal = new ThreadLocal();

        private int i = 1;

        @Override
        public void run() {
            threadLocal.set((int) (Math.random() * 100D));
            i = (int) (Math.random() * 100D);
            System.out.println(Thread.currentThread().getName()+":i="+i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
            System.out.println(threadLocal.get());
            System.out.println("i="+i);
        }
    }

    public static void main(String[] args) {
        MyRunnable sharedRunnableInstance = new MyRunnable();
        Thread thread1 = new Thread(sharedRunnableInstance);
        Thread thread2 = new Thread(sharedRunnableInstance);
        thread1.start();
        thread2.start();
    }
}


