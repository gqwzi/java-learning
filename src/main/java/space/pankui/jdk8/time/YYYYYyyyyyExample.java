package space.pankui.jdk8.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author pankui
 * @date 2019-01-07
 * <pre>
 *
 * </pre>
 */
public class YYYYYyyyyyExample {

    public static void main(String[] args) throws ParseException {

        String YYYY_MM_DD = "YYYY-MM-dd";
        String yyyy_mm_dd = "yyyy-MM-dd";
        LocalDate localDate = LocalDate.of(2018,12,30);

        final DateTimeFormatter YYYY_MM_DD_formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);

        final DateTimeFormatter yyyy_mm_dd_formatter = DateTimeFormatter.ofPattern(yyyy_mm_dd);

        String YYYY_str = YYYY_MM_DD_formatter.format(localDate);
        System.out.println(YYYY_str);

        String yyy_str = yyyy_mm_dd_formatter.format(localDate);
        System.out.println(yyy_str);


        LocalDate localDate_yyyy = LocalDate.parse(yyy_str, yyyy_mm_dd_formatter);
        System.out.println(localDate_yyyy);

        // 使用 YYYY-MM-dd 转换会报异常，得转换yyyy-MM-dd
        //LocalDate localDate_YYYY = LocalDate.parse(YYYY_str, YYYY_MM_DD_formatter);
        // System.out.println(localDate_yyyy);

        Date YYYY_DATE = new SimpleDateFormat(YYYY_MM_DD).parse(YYYY_str);
        System.out.println(YYYY_DATE);
    }
}
