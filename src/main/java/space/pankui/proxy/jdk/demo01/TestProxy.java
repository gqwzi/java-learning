package space.pankui.proxy.jdk.demo01;

/**
 * @author pankui
 * @date 2019-01-17
 * <pre>
 *
 * </pre>
 */
public class TestProxy {
    public static void main(String[] args) {
        BookFacadeProxy proxy = new BookFacadeProxy();
        BookFacade bookProxy = (BookFacade) proxy.bind(new BookFacadeImpl());
        bookProxy.addBook();
    }
}
