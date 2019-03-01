

# java.time 例子

## [代碼](/src/main/java/space/pankui/util/DateUtil.java)

## 日期字符串转换LocalDate（[How to convert String to LocalDate](https://www.mkyong.com/java8/java-8-how-to-convert-string-to-localdate/)）

Few Java examples show you how to convert a String to the new Java 8 Date API – java.time.LocalDate

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

	String date = "16/08/2016";
	
	//convert String to LocalDate
	LocalDate localDate = LocalDate.parse(date, formatter);
``` 

### String = 2016-08-16
 If the String is formatted like ISO_LOCAL_DATE, you can parse the String directly, no need conversion.
    
```java
package com.mkyong.java8.date;

import java.time.LocalDate;

public class TestNewDate1 {

    public static void main(String[] argv) {

        String date = "2016-08-16";

		//default, ISO_LOCAL_DATE
        LocalDate localDate = LocalDate.parse(date);

        System.out.println(localDate);

    }

}
```


## 获取当前时间 区分AM 和PM

```java
//根据时区获取当前时间（不包括日期）
LocalTime localTime = LocalTime.now(ZoneId.of("GMT+02:30"));
//LocalTime localTime = LocalTime.now();
DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
System.out.println(localTime.format(dateTimeFormatter))


```
The output is in AM/PM Format.
``` 
Sample output:  3:00 PM
```

- [参考](https://www.baeldung.com/current-date-time-and-timestamp-in-java-8)

- [参考](https://stackoverflow.com/questions/18734452/display-current-time-in-12-hour-format-with-am-pm/18734539)