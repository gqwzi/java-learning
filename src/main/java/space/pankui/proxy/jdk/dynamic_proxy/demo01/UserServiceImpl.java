package space.pankui.proxy.jdk.dynamic_proxy.demo01;

/**
 * @author pankui
 * @date 2019-01-17
 * <pre>
 *
 * </pre>
 */
public class UserServiceImpl implements UserService {

    /* (non-Javadoc)
     * @see dynamic.proxy.UserService#add()
     */
    @Override
    public void add() {
        System.out.println("--------------------add---------------");
    }
}
