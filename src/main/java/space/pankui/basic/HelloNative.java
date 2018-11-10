package space.pankui.basic;

/**
 * @author pankui
 * @date 2018/11/10
 * <pre>
 * http://www.cnblogs.com/b3051/p/7484501.html
 * </pre>
 */
public class HelloNative {

    static {
        System.loadLibrary("HelloNative");
    }

    public static native void sayHello();

    @SuppressWarnings("static-access")
    public static void main(String[] args) {
        new HelloNative().sayHello();
    }
}
