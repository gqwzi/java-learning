package space.pankui.multi_thread_concurrent;

import java.util.concurrent.Exchanger;

/**
 * @author pankui
 * @date 2018/11/1
 * <pre>
 *      如果2个线程需要交换数据，Exchanger就能派上用场了，见下面的示例
 * </pre>
 */
public class ExchangerDemo {

    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        Thread t1 = new Thread(() -> {
            String temp = "AAAAAA";
            System.out.println("thread 1 交换前：" + temp);
            try {
                temp = exchanger.exchange(temp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread 1 交换后：" + temp);
        });

        Thread t2 = new Thread(() -> {
            String temp = "BBBBBB";
            System.out.println("thread 2 交换前：" + temp);
            try {
                temp = exchanger.exchange(temp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread 2 交换后：" + temp);
        });

        Thread t3 = new Thread(() -> {
            String temp = "CCCCCC";
            System.out.println("thread 3 交换前：" + temp);
            try {
                temp = exchanger.exchange(temp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread 3 交换后：" + temp);
        });

        Thread t4 = new Thread(() -> {
            String temp = "DDDDDD";
            System.out.println("thread 4 交换前：" + temp);
            try {
                temp = exchanger.exchange(temp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread 4 交换后：" + temp);
        });

        t1.start();
        t2.start();
        // 放在前面的会被先交换
        // 相邻的会被交换
        t3.start();
        t4.start();

    }
}
