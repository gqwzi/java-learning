package space.pankui.jdk6;

import java.io.Console;

/**
 * @author pankui
 * @date 30/06/2018
 * <pre>
 *
 * </pre>
 */
public class ConsoleTest {

    public static void main(String[] args) {
        // 获得Console实例，并判断console是否可用
        Console console = System.console();
        if (console != null) {
            // 读取整行字符和密码，密码输入时不会显示
            String user = new String(console.readLine("请输入用户名:"));
            String pwd = new String(console.readPassword("再输入密码:"));
            console.printf("用户名是:" + user + "\n");
            console.printf("密码是:" + pwd + "\n");
        } else {
            System.out.println("Console不可用!");
        }
    }
}
