package space.pankui.basic;

/**
 * @author pankui
 * @date 2018/11/19
 * <pre>
 *   创建对象，查看字节码
 *
 *   通过 "javap -c" 命令查看字节码指令实现
 * </pre>
 */
public class StringDemo03 {

    public static void main(String[] args) {
        String a = "java";
        String c = new String("java");
    }
}
