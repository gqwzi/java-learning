

# 解决Java中由于数据太大自动转换成科学计数法的问题


## 使用BigDecimal类

方式一：
```java

String str=new BigDecimal(num+"").toString();

```

方式二：
```java
String str=new BigDecimal(num.toString()).toString();

```

 