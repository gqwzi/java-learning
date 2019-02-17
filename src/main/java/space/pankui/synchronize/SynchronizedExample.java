package space.pankui.synchronize;

/**
 * @author pankui
 * @date 2019-02-17
 * <pre>
 *
 * </pre>
 */
public class SynchronizedExample {

    public synchronized void test1(){}

    public void test2(){
        synchronized (this){}
    }
}
