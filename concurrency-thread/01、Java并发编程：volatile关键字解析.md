
## [原文](https://www.cnblogs.com/dolphin0520/p/3920373.html)


## Java并发编程：volatile关键字解析


volatile这个关键字可能很多朋友都听说过，或许也都用过。
在Java 5之前，它是一个备受争议的关键字，
因为在程序中使用它往往会导致出人意料的结果。
在Java 5之后，volatile关键字才得以重获生机。

　　volatile关键字虽然从字面上理解起来比较简单，
但是要用好不是一件容易的事情。
由于volatile关键字是与Java的内存模型有关的，
因此在讲述volatile关键之前，我们先来了解一下与内存模型相关的概念和知识，
然后分析了volatile关键字的实现原理，
最后给出了几个使用volatile关键字的场景。


以下是本文的目录大纲：

[一、内存模型的相关概念](02、内存模型的相关概念.md)

 [二.并发编程中的三个概念](03、并发编程中的三个概念.md)

 [三、Java内存模型](04、Java内存模型.md)

 [四、深入剖析volatile关键字](05、深入剖析volatile关键字.md)

 [五、使用volatile关键字的场景](06、使用volatile关键字的场景.md)