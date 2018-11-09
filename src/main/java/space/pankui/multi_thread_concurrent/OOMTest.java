package space.pankui.multi_thread_concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author pankui
 * @date 2018/10/30
 * <pre>
 *  https://www.cnblogs.com/yjmyzz/p/7478266.html
 * </pre>
 */
public class OOMTest {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        int max = 10000;
        List<Person> list = new ArrayList<>(max);
        for (int j = 0; j < max; j++) {
            Person p = new Person();
            p.setAge(100);
            p.setName("菩提树下的杨过");
            list.add(p);
        }
        System.out.println("ready!");
        latch.await();
    }


    public static class Person {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
