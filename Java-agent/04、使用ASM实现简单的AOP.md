
## [原文](https://www.jianshu.com/p/124e0d790466)

# 使用ASM实现简单的AOP

## 前言
之前一直使用greys及其内部升级二次开发版来排查问题。
最近周末刚好事情不多，作为一名程序员本能地想要弄懂这么神奇的greys到底是怎么实现的？
周末从github上拉了代码仔细读了读，其基本技术框架是JVM attach + Instrumentation + asm实现的。
关于JVM attach和Instrumentation的功能，下次再写文章介绍，本文着重于greys中非常神奇的一个类AdviceWeaver，
该类使用asm代码实现了简单的aop功能，本文的实现方式基本参考该类，具体的代码放在了scrat-profiler模块中。
下文将结合asm的使用方法讲解如何实现简单的aop功能。

## asm简介
什么是asm？ASM是一个java字节码操纵框架，它能被用来动态生成类或者增强既有类的功能。
ASM 可以直接产生二进制 class 文件，也可以在类被加载入 Java 虚拟机之前动态改变类行为（摘自网友翻译）。
asm的文档请参考asm文档，文档写的比较全。主要几个重要的类为ClassReader、ClassWriter、ClassVisitor、MethodVisitor等。

 