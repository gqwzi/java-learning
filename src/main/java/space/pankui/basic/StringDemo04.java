package space.pankui.basic;

/**
 * @author pankui
 * @date 2018/11/19
 * <pre>
 *      通过 "javap -c" 命令查看字节码指令实现
 * </pre>
 */
public class StringDemo04 {

    public static void main(String[] args) {
        String a = "hello ";
        String b = "world";
        String c = a + b;
        String d = "hello world";
    }
}
