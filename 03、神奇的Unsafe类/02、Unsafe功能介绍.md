
## [原文](https://tech.meituan.com/2019/02/14/talk-about-java-magic-class-unsafe.html)

# Unsafe功能介绍

![](../images/unsafe/unsafe_function.png)

如上图所示，Unsafe提供的API大致可分为内存操作、
CAS、Class相关、对象操作、线程调度、系统信息获取、内存屏障、数组操作等几类，
下面将对其相关方法和应用场景进行详细介绍。