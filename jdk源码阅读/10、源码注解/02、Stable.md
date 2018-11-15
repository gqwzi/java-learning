
## [原文](https://www.jianshu.com/p/74b2d0246cf2)

# @Stable 注解

使用了Stable注解，这个注解只有用在被根加载器加载的类中才有作用，否则加载器会忽略它。
它用在这里的目的表示当前value中的值是可信任的，Stable用在这里很安全，因为value的值不会为null。

这里看一个例子：
```java

String str = new String();
Field field = str.getClass().getDeclaredField("value");
field.setAccessible(true);
System.out.println(field.get(str)); // 结果为：[B@4157f54e

```
这里我声明了一个空字符串，通过反射，拿到此时的value的值，但是从结果可以看到，value不是null。

