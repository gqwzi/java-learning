package space.pankui.jdk8;

/**
 * @author pankui
 * @date 2018/11/10
 * <pre>
 *
 * </pre>
 */
public class LambdaTest {

    public static void main(String[] args) {
        Func add = (x, y) -> x + y;
        System.out.println(add.exec(1, 2));
    }
}


@FunctionalInterface
interface Func {
    int exec(int x, int y);
}
