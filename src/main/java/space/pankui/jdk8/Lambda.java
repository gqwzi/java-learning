package space.pankui.jdk8;

/**
 * @author pankui
 * @date 2018/11/10
 * <pre>
 *
 * </pre>
 */
@FunctionalInterface
interface Print<T> {
    public void print(T x);
}


public class Lambda {
    public static void PrintString(String s,
                                   Print<String> print) {
        print.print(s);
    }

    /**
     * 编译会报错:conflicts with a compiler-synthesized symbol
     * 与编译器合成的符号冲突
     */
    // private static void lambda$main$0(String s) {}
    public static void main(String[] args) {

         System.setProperty("jdk.internal.lambda.dumpProxyClasses", "/kobe/learn/project/java/java-learning/out/production/classes/space/pankui/jdk8/");

        PrintString("test", (x) -> System.out.println(x));
    }
}
