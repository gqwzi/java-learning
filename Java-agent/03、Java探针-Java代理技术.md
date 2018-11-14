
## [原文](https://www.jianshu.com/p/7ea5f5aca75b)

# Java探针-Java代理技术

## 总结：

- 使用java代理来实现java字节码注入

- 使用JavaSsist可以对字节码进行修改

- 使用ASM可以修改字节码

使用Java代理和ASM字节码技术开发java探针工具可以修改字节码

备注：javassist是一个库，实现ClassFileTransformer接口中的transform()方法。
ClassFileTransformer 这个接口的目的就是在class被装载到JVM之前将class字节码转换掉，从而达到动态注入代码的目的。

备注：ASM是一个java字节码操纵框架，它能被用来动态生成类或者增强既有类的功能。
ASM 可以直接产生二进制 class 文件，也可以在类被加载入 Java 虚拟机之前动态改变类行为。
Java class 被存储在严格格式定义的 .class文件里，
这些类文件拥有足够的元数据来解析类中的所有元素：类名称、方法、属性以及 Java 字节码（指令）。
ASM从类文件中读入信息后，能够改变类行为，分析类信息，甚至能够根据用户要求生成新类。

 