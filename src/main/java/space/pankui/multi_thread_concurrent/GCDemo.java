package space.pankui.multi_thread_concurrent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankui
 * @date 2018/10/30
 * <pre>
 *
 * </pre>
 */
public class GCDemo {
    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<>();
        while (true) {
            Thread.sleep(10);
            list.add("菩提树下的杨过" + System.currentTimeMillis());
        }
    }
}
