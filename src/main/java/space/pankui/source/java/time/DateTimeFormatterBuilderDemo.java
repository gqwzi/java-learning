package space.pankui.source.java.time;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Locale;

/**
 * @author pankui
 * @date 2019-01-14
 * <pre>
 *
 * </pre>
 */
public class DateTimeFormatterBuilderDemo {

    public static void main(String args[]) {
        String d2arr[] = {
                "2016-12-21",
                "1/17/2016",
                "1/3/2016",
                "11/23/2016",
                "OCT 20 2016",
                "Oct 22 2016",
                "Oct 23", // default year is 2016
                "OCT 24",  // default year is 2016
        };

        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                // 如果有年，下面这两行可以去掉
                .parseCaseInsensitive().parseLenient()
                .parseDefaulting(ChronoField.YEAR_OF_ERA, 2016L)
                .appendPattern("[yyyy-MM-dd]")
                .appendPattern("[M/dd/yyyy]")
                .appendPattern("[M/d/yyyy]")
                .appendPattern("[MM/dd/yyyy]")
                .appendPattern("[MMM dd yyyy]")
                .appendPattern("[MMM dd]");

        DateTimeFormatter formatter2 = builder.toFormatter(Locale.ENGLISH);
        for (String d2 : d2arr) {
            try {
                LocalDate date = LocalDate.parse(d2, formatter2);
                System.out.printf("%s%n", date);
            } catch (DateTimeParseException e) {
                System.out.printf("%s is not parsable! %n", d2);
                throw e;
            }
        }
    }
}
