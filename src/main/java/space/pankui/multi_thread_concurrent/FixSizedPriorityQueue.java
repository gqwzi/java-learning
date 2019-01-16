package space.pankui.multi_thread_concurrent;

import java.util.*;

/**
 * @author pankui
 * @date 2019-01-16
 * <pre>
 *
 * 固定容量的优先队列，模拟大顶堆，用于解决求topN小的问题
 *
 *
 * PriorityQueue优先队列：
 * 数据描述：PriorityQueue是从JDK1.5开始提供的新的数据结构接口，它是一种基于优先级堆的极大优先级队列。
 * 优先级队列是不同于先进先出队列的另一种队列。每次从队列中取出的是具有最高优先权的元素。
 * 如果不提供Comparator的话，优先队列中元素默认按自然顺序排列，也就是数字默认是小的在队列头，
 * 字符串则按字典序排列（参阅 Comparable），也可以根据 Comparator 来指定，这取决于使用哪种构造方法。
 * 优先级队列不允许 null 元素。依靠自然排序的优先级队列还不允许插入不可比较的对象（这样做可能导致 ClassCastException）。
 *
 * 实现步骤：PriorityQueue构造固定容量的优先队列，模拟大顶堆，这种队列本身数组实现，无容量限制，
 * 可以指定队列长度和比较方式，然后将数据依次压入，当队列满时会poll出小值，最后需要注意的是，
 * priorityQueue本身遍历是无序的，可以使用内置poll()方法，每次从队首取出元素。
 *
 * </pre>
 */
public class FixSizedPriorityQueue <E extends Comparable> {

    private PriorityQueue<E> queue;
    private int maxSize; // 堆的最大容量

    public FixSizedPriorityQueue(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.maxSize = maxSize;
        this.queue = new PriorityQueue(maxSize, new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                // 生成最大堆使用o2-o1,生成最小堆使用o1-o2, 并修改 e.compareTo(peek) 比较规则
                return (o2.compareTo(o1));
            }
        });
    }

    public void add(E e) {
        // 未达到最大容量，直接添加
        if (queue.size() < maxSize) {
            queue.add(e);
        } else { // 队列已满
            E peek = queue.peek();
            // 将新元素与当前堆顶元素比较，保留较小的元素
            if (e.compareTo(peek) < 0) {
                queue.poll();
                queue.add(e);
            }
        }
    }


    public static void main(String[] args) {

        final FixSizedPriorityQueue pq = new FixSizedPriorityQueue(10);
        Random random = new Random();
        int rNum = 0;
        System.out.println("100 个 0~999 之间的随机数：-----------------------------------");
        for (int i = 1; i <= 100; i++) {
            rNum = random.nextInt(1000);
            System.out.println(rNum);
            pq.add(rNum);
        }
        System.out.println("PriorityQueue 本身的遍历是无序的：-----------------------------------");
        Iterable<Integer> iter = new Iterable<Integer>() {

            @Override
            public Iterator<Integer> iterator() {
                return pq.queue.iterator();
            }
        };
        for (Integer item : iter) {
            System.out.print(item + ", ");
        }
        System.out.println();
        System.out.println("PriorityQueue 排序后的遍历：-----------------------------------");
        /*
         * for (Integer item : pq.sortedList()) { System.out.println(item); }
         */
        // 或者直接用内置的 poll() 方法，每次取队首元素（堆顶的最大值）
        while (!pq.queue.isEmpty()) {
            System.out.print(pq.queue.poll() + ", ");
        }
    }


}
