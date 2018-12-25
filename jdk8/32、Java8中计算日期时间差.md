
## [原文](https://blog.csdn.net/hspingcc/article/details/73332526)

# Java8中计算日期时间差

## 一.简述

在Java8中，我们可以使用以下类来计算日期时间差异：

- 1.Period

- 2.Duration

- 3.ChronoUnit

## 二.Period类
主要是Period类方法getYears（），getMonths（）和getDays（）来计算.

示例:
```java

package insping;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;

public class Test {

    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        System.out.println("Today : " + today);
        LocalDate birthDate = LocalDate.of(1993, Month.OCTOBER, 19);
        System.out.println("BirthDate : " + birthDate);

        Period p = Period.between(birthDate, today);
        System.out.printf("年龄 : %d 年 %d 月 %d 日", p.getYears(), p.getMonths(), p.getDays());
    }
}

```

结果:
```
Today : 2017-06-16
BirthDate : 1993-10-19
年龄 : 23 年 7 月 28 日
```

## 三.Duration类

提供了使用基于时间的值（如秒，纳秒）测量时间量的方法。 

示例:
```java

package insping;

import java.time.Duration;
import java.time.Instant;

public class Test {

    public static void main(String[] args) {
        Instant inst1 = Instant.now();
        System.out.println("Inst1 : " + inst1);
        Instant inst2 = inst1.plus(Duration.ofSeconds(10));
        System.out.println("Inst2 : " + inst2);

        System.out.println("Difference in milliseconds : " + Duration.between(inst1, inst2).toMillis());

        System.out.println("Difference in seconds : " + Duration.between(inst1, inst2).getSeconds());

    }
}

```

结果:
```
Inst1 : 2017-06-16T07:46:45.085Z
Inst2 : 2017-06-16T07:46:55.085Z
Difference in milliseconds : 10000
Difference in seconds : 10
```

## 四.ChronoUnit类

ChronoUnit类可用于在单个时间单位内测量一段时间，例如天数或秒。 

以下是使用between（）方法来查找两个日期之间的区别的示例。
```java

package insping;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

public class Test {

    public static void main(String[] args) {
        LocalDate startDate = LocalDate.of(1993, Month.OCTOBER, 19);
        System.out.println("开始时间  : " + startDate);

        LocalDate endDate = LocalDate.of(2017, Month.JUNE, 16);
        System.out.println("结束时间 : " + endDate);

        long daysDiff = ChronoUnit.DAYS.between(startDate, endDate);
        System.out.println("两天之间的差在天数   : " + daysDiff);

    }
}


```
结果:

```
开始时间  : 1993-10-19
结束时间 : 2017-06-16
两天之间的差在天数   : 8641

```

