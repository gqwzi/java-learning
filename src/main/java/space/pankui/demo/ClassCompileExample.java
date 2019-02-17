package space.pankui.demo;

import java.util.ArrayList;

/**
 * @author pankui
 * @date 2019-02-17
 * <pre>
 *
 * </pre>
 */
public class ClassCompileExample {

    public static void main(String[] args) {
        Example1 example1 = new Example1();
        Example2 example2 = new Example2();

        // 这里问题
        // Exception in thread "main" java.lang.IndexOutOfBoundsException: Index: 1, Size: 0
        ArrayList<String> array = new ArrayList<>();
        array.add(1, "hello world");

    }
}
    class Example1 {
        public Example1() {
            System.out.println(this.getClass().getName());
        }
    }

    class Example2 {
        public Example2() {
            System.out.println(this.getClass().getName());
        }
    }

