package space.pankui.multi_thread_concurrent.lock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author pankui
 * @date 2018/9/25
 * <pre>
 * https://www.cnblogs.com/skywang12345/p/3505809.html
 *
 *
 *
 * 结果说明：
 * (01) 观察Thread0和Thread-2的运行结果，我们发现，Thread-0启动并获取到“读取锁”，在它还没运行完毕的时候，Thread-2也启动了并且也成功获取到“读取锁”。
 * 因此，“读取锁”支持被多个线程同时获取。
 * (02) 观察Thread-1,Thread-3,Thread-5这三个“写入锁”的线程。只要“写入锁”被某线程获取，则该线程运行完毕了，才释放该锁。
 * 因此，“写入锁”不支持被多个线程同时获取。
 *
 * </pre>
 */
public class ReentrantReadWriteLockDemo01 {


    public static void main(String[] args) {

        //创建账户
        MyCount myCount = new MyCount("123",10000);

        //创建用户，并指定账户
        User user = new User("Hi",myCount);
        for (int i = 0; i<3; i++) {
            user.getCash();
            user.setCash((i + 1)* 1000);
        }
    }

}


class User {

    //用户名
    private String name;

    //所要操作的账号
    private MyCount myCount;

    //执行操作所需要的对象
    private ReadWriteLock readWriteLock;

    User(String name,MyCount myCount) {
        this.name = name;
        this.myCount = myCount;
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public void getCash() {
        new Thread(){
            @Override
            public void run() {
                readWriteLock.readLock().lock();
                try {
                    System.out.println(Thread.currentThread().getName()+" getCash start");
                    myCount.getCash();
                    Thread.sleep(1);
                    System.out.println(Thread.currentThread().getName()+"getCash end");

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    readWriteLock.readLock().unlock();
                }
            }
        }.start();

    }

    public void setCash(final int cash) {

        new Thread(){
            @Override
            public void run() {
                readWriteLock.writeLock().lock();
                try {

                    System.out.println(Thread.currentThread().getName()+" setCash start");
                    myCount.setCash(cash);
                    Thread.sleep(1);
                    System.out.println(Thread.currentThread().getName()+" setCash end");


                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        }.start();
    }

}

class MyCount {

    //账号
    private String id ;

    //账户余额
    private int cash;

    MyCount(String id,int cash) {
        this.id = id ;
        this.cash = cash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCash() {
        System.out.println(Thread.currentThread().getName()+" getCash cash="+cash);
        return cash;
    }

    public void setCash(int cash) {
        System.out.println(Thread.currentThread().getName() +"setCash cash="+cash);
        this.cash = cash;
    }
}
