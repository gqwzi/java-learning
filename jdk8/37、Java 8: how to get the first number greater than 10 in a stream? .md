
## [原文](https://stackoverflow.com/questions/30290234/java-8-how-to-get-the-first-number-greater-than-10-in-a-stream)

# Java 8: how to get the first number greater than 10 in a stream?

获取第一个比 10 大的数字

```java
 .filter(i -> i > 10).findFirst();
```

