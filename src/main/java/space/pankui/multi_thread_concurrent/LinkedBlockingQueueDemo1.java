package space.pankui.multi_thread_concurrent;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author pankui
 * @date 2018/10/13
 * <pre>
 * LinkedBlockingQueue是“线程安全”的队列，而LinkedList是非线程安全的。
 *
 *   下面是“多个线程同时操作并且遍历queue”的示例
 *   (01) 当queue是LinkedBlockingQueue对象时，程序能正常运行。
 *   (02) 当queue是LinkedList对象时，程序会产生ConcurrentModificationException异常。
 * </pre>
 */
public class LinkedBlockingQueueDemo1 {

    // TODO: queue是LinkedList对象时，程序会出错。
    //private static Queue<String> queue = new LinkedList<String>();
    private static Queue<String> queue = new LinkedBlockingQueue<>();
    public static void main(String[] args) {

        // 同时启动两个线程对queue进行操作！
        new MyThread("ta").start();
        new MyThread("tb").start();
    }

    private static void printAll() {
        String value;
        Iterator iter = queue.iterator();
        while(iter.hasNext()) {
            value = (String)iter.next();
            System.out.print(value+", ");
        }
        System.out.println();
    }

    private static class MyThread extends Thread {
        MyThread(String name) {
            super(name);
        }
        @Override
        public void run() {
            int i = 0;
            // 由于 i++ 不是线程安全，因此会有多个1 或者其他只情况
            while (i++ < 6) {

                // “线程名” + "-" + "序号"
                String val = Thread.currentThread().getName()+i;
                System.out.println("val="+val);
                queue.add(val);
                // 通过“Iterator”遍历queue。
                printAll();

                System.out.println("========= i="+i);

            }
        }
    }
}
