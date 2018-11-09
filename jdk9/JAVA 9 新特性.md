
# JDK 9新特性汇总

期待已久的JDK 9发布了，有哪些新特性值得我们去体验呢？小编给你整理了JDK 9的新特性。 


## JDK9的关键更改：

Java平台模块化系统：引入了一种新的Java编程组件模块，它是一个命名的、自描述的代码和数据集合。

java9的模块化，从一个独立的开源项目而来，名为Jigsaw。

## JDK 9中的新工具：

1、Java Shell

2、添加更多的诊断命令

3、删除启动时间JRE版本选择

4、jlink:Java连接器

5、多版本兼容Jar

## JDK 9中新的安全性： 

1、数据报传输层安全性(DTLS）

2、禁用sha - 1证书

## JDK 9中核心库的新内容:

1、进程API更新：新增ProcessHandle类，该类提供进程的本地进程ID、参数、命令、启动时间、累计CPU时间、用户、父进程和子进程。
这个类还可以监控进程的活力和破坏进程。ProcessHandle。onExit方法，当进程退出时，复杂未来类的异步机制可以执行一个操作。 

2、更多的并发更新：包括一个可互操作的发布-订阅框架，以及对CompletableFuture API的增强。

3、便利的工厂方法对于Collections：用少量的元素创建集合和映射的实例更容易。
在列表、设置和映射接口上的新静态工厂方法使创建这些集合的不可变实例变得更加简单 例子：Set<String> alphabet = Set.of("a", "b", "c");

写在最后：

··    ​如果想体验JDK 9新特性，马上去Oracle下载JDK 9安装文件吧！


### [Java 9 揭秘（20. JDK 9中API层次的改变）](https://www.cnblogs.com/IcanFixIt/p/7271461.html)