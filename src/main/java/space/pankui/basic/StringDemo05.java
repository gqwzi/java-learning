package space.pankui.basic;

/**
 * @author pankui
 * @date 2018/11/19
 * <pre>
 *通过 "javap -c" 命令查看字节码指令实现
 * </pre>
 */
public class StringDemo05 {

    public static void main(String[] args) {
        final String a = "hello ";
        final String b = "world";
        String c = a + b;
        String d = "hello world";
    }
}
