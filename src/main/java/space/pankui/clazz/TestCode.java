package space.pankui.clazz;

/**
 * @author pankui
 * @date 2018/11/1
 * <pre>
 *
 * </pre>
 */
public class TestCode {
    public static void main(String[] args) {
        int result = new TestCode().foo();
        System.out.println(result);
    }
    public int foo() {
        int x ;
        try {
            x = 1;
            return x;
        } catch (Exception e) {
            x = 2;
            return x;
        } finally {
            x = 3;
        }
    }

}
