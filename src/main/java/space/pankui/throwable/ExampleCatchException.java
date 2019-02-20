package space.pankui.throwable;

/**
 * @author pankui
 * @date 2019-02-20
 * <pre>
 *
 * </pre>
 */
public class ExampleCatchException {

    public void catchException() {
        long l = System.nanoTime();
        for (int i = 0; i < 2; i++) {
            try {
                throw new Exception();
            } catch (Exception e) {
                //nothing to do
            }
        }
        System.out.println("抛出并捕获异常：" + (System.nanoTime() - l));
    }
}
