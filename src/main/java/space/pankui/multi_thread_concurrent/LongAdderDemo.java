package space.pankui.multi_thread_concurrent;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author pankui
 * @date 2018/10/29
 * <pre>
 *
 * </pre>
 */
public class LongAdderDemo {

    public static void main(String[] args) {

        LongAdder  longAdder = new LongAdder();

        longAdder.add(Long.valueOf("1"));
        longAdder.add(100L);

        System.out.println(longAdder.sum());
    }
}
