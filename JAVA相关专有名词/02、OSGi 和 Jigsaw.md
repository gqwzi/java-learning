

# OSGi 和 Jigsaw

## Jigsaw

Jigsaw是OpenJDK项目下的一个子项目，旨在为Java SE平台设计、
实现一个标准的模块系统，并应用到该平台和JDK中。
该项目由Java编程语言编译器小组赞助。

## OSGI
OSGi（Open Service Gateway Initiative）有双重含义。

一方面它指OSGi Alliance组织；

另一方面指该组织制定的一个基于Java语言的服务（业务）规范——OSGi服务平台（Service Platform）。

- OSGi的主要职责就是为了让开发者能够建动态化、模块化的Java系统。


## [为什么 Java 9 模块化使用 Jigsaw 而不是 OSGi？](https://www.zhihu.com/question/40413806/answer/87168008)

“由于模块化规范主导权的重要性，Sun公司不能接受一个无法由它控制的规范，
在整个Java SE 6期间都拒绝把任何模块化技术内置到JDK之中。

在Java SE 7发展初期，Sun公司再次提交了一个新的规范请求文档JSR-294：
Java编程语言中的改进模块性支持（Improved Modularity Support in the Java Programming Language），
尽管这个JSR仍然没有通过，
但是Sun公司已经独立于JCP专家组在OpenJDK里建立了一个名为Jigsaw（拼图）的子项目来推动这个规范在Java平台中转变为具体的实现。”　　　　　　　　　　　　　　　　　　　　　　　　　　————《深入理解Java虚拟机》

[原文回答](https://www.zhihu.com/question/40413806/answer/135807272)

https://www.zhihu.com/question/40413806/answer/135807272


IBM一直想说服大家别搞什么Jigsaw了，
直接把OSGi放到Java核心平台的标准里不就好了嘛，
或者说(OSGi--)++ ——对OSGi做适度裁剪然后再加点新功能。
不过现在大家都知道结果如何了 >_<

[原文](https://www.zhihu.com/question/39112373)
 
 