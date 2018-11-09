package space.pankui.jdk5;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author pankui
 * @date 2018/7/1
 * <pre>
 *
 * </pre>
 */
public class FormatTester {

    /**
     * 格式化.
     */
    private static void formatter() {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);

        // 可重新排序输出.
        // -> " d  c  b  a"
        formatter.format("%n%4$2s %3$2s %2$2s %1$2s %n", "a", "b", "c", "d");
        // -> "e =    +2,7183"
        formatter.format(Locale.FRANCE, "e = %+10.4f", Math.E);

        formatter.format("%nAmount gained or lost since last statement: $ %(,.2f", 6217.58);
        // -> "Amount gained or lost since last statement: $ 6,217.58"

        System.out.println("打印出格式化后的字符串:"+ formatter);
        formatter.close();
    }

    /**
     * printf打印.
     */
    private static void printf() {
        String filename = "testfile";
        try (FileReader fileReader = new FileReader(filename)) {
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            int i = 1;
            while ((line = reader.readLine()) != null) {
                System.out.printf("Line %d: %s%n", i++, line);
            }
        } catch (Exception e) {
            System.err.printf("Unable to open file named '%s': %s", filename, e.getMessage());
        }
    }

    /**
     * stringFormat使用.
     */
    private static void stringFormat() {
        // 格式化日期.
        Calendar c = new GregorianCalendar(1995, Calendar.MAY, 23);
        String s = String.format("Duke's Birthday: %1$tm %1$te,%1$tY", c);
        // -> s == "Duke's Birthday: May 23, 1995"
        System.out.println(s);
    }

    /**
     * Formatter 格式化消息.
     */
    private static void messageFormat() {
        String msg = "欢迎光临，当前（{0}）等待的业务受理的顾客有{1}位，请排号办理业务！ {2}";
        MessageFormat mf = new MessageFormat(msg);
        String fmsg = mf.format(new Object[]{new Date(), 35,"good day"});
        System.out.println(fmsg);
    }

    /**
     * 格式化日期.
     * Format
     */
    private static void dateFormat() {
        String str = "2010-1-10 17:39:21";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            System.out.println("格式化后的日期:"+ format.format(format.parse(str)));
        } catch (Exception e) {
            System.out.println("日期格式化出错！"+ e);
        }
    }

    public static void main(String[] args) {
        formatter();
        stringFormat();
        messageFormat();
        dateFormat();
        printf();
    }

}
