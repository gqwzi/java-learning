package space.pankui.multi_thread_concurrent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author pankui
 * @date 2018/11/1
 * <pre>
 *     https://www.cnblogs.com/yjmyzz/p/java-concurrent-tools-sample.html
 * 信号量（Semaphore）
 *
 * 适用场景：用于资源数有限制的并发访问场景。
 * </pre>
 */
public class SemaphoreDemo {

    public static void main(String[] args) throws InterruptedException {
        BoundedHashSet<String> set = new BoundedHashSet<>(5);
        for (int i = 0; i < 6; i++) {
            if (set.add(i + "")) {
                System.out.println(i + " added !");
            } else {
                System.out.println(i + " not add to Set!");
            }
        }
    }
}

class BoundedHashSet<T> {
    private final Set<T> set;
    private final Semaphore semaphore;

    public BoundedHashSet(int bound) {
        this.set = Collections.synchronizedSet(new HashSet<T>());
        this.semaphore = new Semaphore(bound);
    }

    public boolean add(T t) throws InterruptedException {
        if (!semaphore.tryAcquire(5, TimeUnit.SECONDS)) {
            return false;
        }
        ;
        boolean added = false;
        try {
            added = set.add(t);
            return added;
        } finally {
            if (!added) {
                semaphore.release();
            }
        }
    }

    public boolean remove(Object o) {
        boolean removed = set.remove(o);
        if (removed) {
            semaphore.release();
        }
        return removed;
    }
}
