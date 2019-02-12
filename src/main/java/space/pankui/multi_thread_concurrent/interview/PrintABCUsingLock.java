package space.pankui.multi_thread_concurrent.interview;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author pankui
 * @date 2019-02-12
 * <pre>
 *   三个线程分别打印A，B，C，要求这三个线程一起运行，打印n次，输出形如“ABCABCABC....”的字符串。
 * </pre>
 */
public class PrintABCUsingLock {

    private int state;

    private int times;

    private Lock lock = new ReentrantLock();

    PrintABCUsingLock(int times) {
        this.times = times;
    }

    public static void main(String[] args) {
        PrintABCUsingLock printABC = new PrintABCUsingLock(10);
        new Thread(printABC::printA).start();
        new Thread(printABC::printC).start();
        new Thread(printABC::printB).start();
    }

    private void printA(){
        print("A", 0);
    }

    private void printB(){
        print("B", 1);
    }

    private void printC(){
        print("C", 2);
    }

    private void print(String name, int targetState) {

       for (int i = 0 ;i < times;) {
           // 获取锁
           lock.lock();
           if (state % 3 == targetState) {
               state ++;
               i++;
               System.out.print(name);
           }
           lock.unlock();
       }

    }

}
