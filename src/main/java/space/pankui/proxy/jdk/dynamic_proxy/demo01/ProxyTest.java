package space.pankui.proxy.jdk.dynamic_proxy.demo01;

/**
 * @author pankui
 * @date 2019-01-17
 * <pre>
 *
 * </pre>
 */
public class ProxyTest {

    public static void main(String[] args) {
        // 实例化目标对象
        UserService userService = new UserServiceImpl();

        // 实例化InvocationHandler
        MyInvocationHandler invocationHandler = new MyInvocationHandler(userService);

        // 根据目标对象生成代理对象
        UserService proxy = (UserService) invocationHandler.getProxy();

        // 调用代理对象的方法
        proxy.add();

    }
}
