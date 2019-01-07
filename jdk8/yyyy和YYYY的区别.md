
## [原文](https://blog.csdn.net/bewilderment/article/details/48391717)


## [原文]()

# yyyy和YYYY的区别


Y表示的是Week year
 
YYYY是表示：当天所在的周属于的年份，一周从周日开始，周六结束，只要本周跨年，那么这周就算入下一年。

当天所在的周属于的年份，一周从周日开始，周六结束，只要本周跨年，那么这周就算入下一年。
这个结论在正向转换的时候是没有问题的，但是在逆向转换的时候是有问题的。

### [例子](/src/main/java/space/pankui/jdk8/time/YYYYYyyyyyExample.java)
```java

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

        // 这里逆向转化还是原来的
        Date YYYY_DATE = new SimpleDateFormat(YYYY_MM_DD).parse(YYYY_str);
        System.out.println(YYYY_DATE);
```
结果：

```java
2019-12-30
2018-12-30
2018-12-30
Sun Dec 30 00:00:00 CST 2018
```

###   
