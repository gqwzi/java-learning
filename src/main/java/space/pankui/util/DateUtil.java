package space.pankui.util;


import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author pankui
 * @date 2018/11/15
 * <pre>
 *    使用例子：
 *     获取当前周周一开始时间
 *          DateUtil.getMondayMinDateTimeByWeek(0);
 *     获取下下周周日结束时间
 *         DateUtil.getEndSundayOfWeek(2);
 *     获取上周周日结束时间
 *         DateUtil.getEndSundayOfWeek(-1);
 * </pre>
 */
public class DateUtil {


    public static final String YYYY_MM_DD_HH_mm_SS = "YYYY-MM-dd HH:mm:ss";

    public static final String HH_mm = "HH:mm";

    public static final String YYYY_MM_DD = "YYYY-MM-dd";

    public static final String YYYYMMDD = "YYYYMMdd";


    /**
     * 获取周一开始时间 包含日期+时间
     * 00：00：00
     */
    public static LocalDateTime getMondayMinDateTimeByWeek(long week) {

        LocalDate localDate = getLocalDateByDayOfWeekAndWeek(DayOfWeek.MONDAY, week);
        LocalTime localTime = LocalTime.of(LocalTime.MIN.getHour(), LocalTime.MIDNIGHT.getMinute());

        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * 获取 当前周 周日的结束时间 包含日期+时间
     * 23:59：59
     * 2018-12-23T23:59:59
     */
    public static LocalDateTime getSundayMaxDateTimeByWeek(long week) {

        LocalDate localDate = getLocalDateByDayOfWeekAndWeek(DayOfWeek.SUNDAY, week);
        LocalTime localTime = LocalTime.of(LocalTime.MAX.getHour(),
                LocalTime.MAX.getMinute(), LocalTime.MAX.getSecond());

        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * 获取最大时间 23:59:59
     */
    public static LocalTime getMaxLocalTime() {
        return LocalTime.of(LocalTime.MAX.getHour(),
                LocalTime.MAX.getMinute(), LocalTime.MAX.getSecond());
    }

    /**
     * 获取最小的时间 0:0:0
     */
    public static LocalTime getMinLocalTime() {
        return LocalTime.of(LocalTime.MIN.getHour(),
                LocalTime.MIDNIGHT.getMinute(), LocalTime.MIN.getSecond());

    }

    /**
     * 获取周第一天
     * 中国 从周一开始
     * week 1 就是下周 -1 上周，0是当前周
     */
    public static LocalDate getMondayByWeek(long week) {
        return getLocalDateByDayOfWeekAndWeek(DayOfWeek.MONDAY, week);
    }

    public static LocalDate getSundayByWeek(long week) {
        return getLocalDateByDayOfWeekAndWeek(DayOfWeek.SUNDAY, week);
    }

    public static LocalDate getFridayByWeek(long week) {
        return getLocalDateByDayOfWeekAndWeek(DayOfWeek.FRIDAY, week);
    }

    public static LocalDate getSaturdayByWeek(long week) {
        return getLocalDateByDayOfWeekAndWeek(DayOfWeek.SATURDAY, week);
    }

    /**
     * 暂时不考虑时区问题
     * <p>
     * 获取某周的星期几
     * week 1 就是下周 -1 上周，0是当前周
     */
    public static LocalDate getLocalDateByDayOfWeekAndWeek(DayOfWeek dayOfWeek, long week) {

        LocalDate localDate = LocalDate.now();
        localDate = localDate.with(dayOfWeek);
        if (week != 0) {
            localDate = localDate.plusWeeks(week);
        }
        return localDate;
    }

    /**
     * 比如今天是 2018-12-19
     * 如果是获取上上周三(12月05号)的周日日期那么 day = -14
     * <p>
     * 获取某天的这周日
     */
    public static LocalDate getSundayByDay(long day) {

        LocalDate localDate = getDayDate(day);
        //获取当前日期周日,获取上一周周日
        localDate = localDate.plusWeeks(-1);
        localDate = localDate.with(DayOfWeek.SUNDAY);
        return localDate;
    }

    public static LocalDate getMondayByDay(long day) {

        LocalDate localDate = getDayDate(day);
        localDate = localDate.with(DayOfWeek.MONDAY);
        return localDate;
    }


    public static String getSundayMaxTimeByDay(long day) {
        LocalDate localDate = getSundayByDay(day);
        LocalTime endLocalTime = LocalTime.of(23, 30, 0);
        LocalDateTime end = LocalDateTime.of(localDate, endLocalTime);
        return parseLocalDateTimeToString(end, YYYY_MM_DD_HH_mm_SS);
    }

    /**
     * 返回 YYYYMMdd
     */
    public static String getNowDateStr() {
        return getNowDateStr(YYYYMMDD);
    }

    public static String getDateByDay(long day) {
        LocalDate localDate = getDayDate(day);
        return parseLocalDateToString(localDate, YYYY_MM_DD);
    }

    public static String getNowDateStr(String pattern) {
        LocalDate localDate = LocalDate.now();
        return parseLocalDateToString(localDate, pattern);
    }

    public static String parseLocalDateToString(LocalDate localDate, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(dateTimeFormatter);
    }

    public static String getMondayMinDateTimeStrByWeek(long week) {
        LocalDateTime localDateTime = getMondayMinDateTimeByWeek(week);
        return parseLocalDateTimeToString(localDateTime, YYYY_MM_DD_HH_mm_SS);
    }


    public static String getSundayMaxDateTimeStrByWeek(long week) {
        LocalDateTime localDateTime = getSundayMaxDateTimeByWeek(week);
        return parseLocalDateTimeToString(localDateTime, YYYY_MM_DD_HH_mm_SS);
    }

    public static String parseLocalDateTimeToString(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_mm_SS);
        return localDateTime.format(dateTimeFormatter);
    }

    public static String parseLocalDateTimeToString(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(dateTimeFormatter);
    }

    public static String parseLocalTimeToString(LocalTime localTime, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return localTime.format(dateTimeFormatter);
    }

    public static LocalDateTime parseTimestampToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }


    /**
     * 获取 day 天日期
     * 0 是当天，1 明天，-1 是昨天
     */
    public static LocalDate getDayDate(long day) {
        LocalDate localDate = LocalDate.now();
        if (day != 0) {
            localDate = localDate.plusDays(day);
        }
        return localDate;
    }

    /**
     * 获取 早上8 点到晚上23点 所有整点及其半点
     */
    public static List<String> listFullHalfHour() {

        LocalTime startLocalTime = LocalTime.of(8, 0, 0);
        LocalTime endLocalTime = LocalTime.of(23, 30, 0);
        List<String> list = List.of();

        while (endLocalTime.isAfter(startLocalTime)) {
            LocalTime localTime = LocalTime.of(startLocalTime.getHour(), startLocalTime.getMinute());
            String strTime = parseLocalTimeToString(localTime, HH_mm);
            list.add(strTime);
            startLocalTime = startLocalTime.plusMinutes(30);
        }
        return list;
    }

    /**
     * 获取 周一到周日的日期 YYYY_MM_DD 格式
     * 当前周 0
     * 上一周 -1
     * 下周 1
     */
    public static List<String> listWeekByWeek(long week) {

        LocalDate monday = getMondayByWeek(week);
        LocalDate sunday = getSundayByWeek(week);
        List<String> list = List.of();
        while (sunday.isAfter(monday)) {

            LocalDate localDate = monday;
            String result = parseLocalDateToString(localDate, YYYY_MM_DD);
            list.add(result);
            monday = monday.plusDays(1);
        }
        String result = parseLocalDateToString(monday, YYYY_MM_DD);
        list.add(result);
        return list;
    }


    /**
     * 判断日期是否周一,因为周一的数据抓不到
     */
    public static boolean isCurrentWeekMonday(long day) {

        LocalDate localDate = getDayDate(day);

        LocalDate monday = getMondayByDay(day);

        if (monday.equals(localDate)) {
            return true;
        }
        return false;
    }

    public static String parseTimeStampToString(Long time) {
        Timestamp timestamp = new Timestamp(time);
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return parseLocalDateTimeToString(localDateTime, YYYY_MM_DD);
    }

    /**
     * 日期相差多少周
     * <p>
     * 注意：如果 拿create 减去当前时间周一，因此负数就是相差 负几周!
     *
     * @return
     */
    public static long getTwoDateSubtractionWeek(LocalDate createTime) {
        //参数是创建时间，然后获取创建时间所在周的周一
        createTime = createTime.with(DayOfWeek.MONDAY);

        //获取当前时间所在的周一
        LocalDate now = LocalDate.now().with(DayOfWeek.MONDAY);
        // 相减相差多少周
        return ChronoUnit.WEEKS.between(now, createTime);
    }

}
