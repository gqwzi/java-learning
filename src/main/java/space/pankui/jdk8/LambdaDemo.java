package space.pankui.jdk8;

/**
 * @author pankui
 * @date 2018/11/10
 * <pre>
 *
 * </pre>
 */

public class LambdaDemo {

    public static void PrintString(String s, Print_v2<String> print) {
        print.print(s);
    }

    public static void main(String[] args) {
        PrintString("test", (x) -> System.out.println(x));
    }
}