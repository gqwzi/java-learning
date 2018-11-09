
# [原文](http://www.infoq.com/cn/news/2018/03/Java-10-new-features)

北京时间2018年 3 月 21 日，Oracle 官方宣布 Java 10 正式发布。
这是 Java 大版本周期变化后的第一个正式发布版本（详见这里），非常值得关注。
你可以点[击以下地址即刻下载](http://www.oracle.com/technetwork/java/javase/downloads/index.html
)：


去年 9 月，Oracle 将 Java 大版本周期从原来的 2-3 年，调整成每半年发布一个大的版本。
而版本号仍延续原来的序号，即 Java 8、Java 9、Java 10、Java 11.....

但和之前不一样的是，同时还有一个版本号来表示发布的时间和是否为 LTS（长期支持版本），比如 Java 10 对应 18.3。如下示例：


```java
/jdk-10/bin$ ./java -version

openjdk version "10" 2018-03-20

OpenJDK Runtime Environment 18.3 (build 10+46)

OpenJDK 64-Bit Server VM 18.3 (build 10+46, mixed mode)
```

需要注意的是 Java 9 和 Java 10 都不是 LTS 版本。
和过去的 Java 大版本升级不同，这两个只有半年左右的开发和维护期。
而未来的 Java 11，也就是 18.9 LTS，才是 Java 8 之后第一个 LTS 版本（得到 Oracle 等商业公司的长期支持服务）。

这种发布模式已经得到了广泛应用，一个成功的例子就是 Ubuntu Linux 操作系统，在偶数年 4 月的发行版本为 LTS，会有很长时间的支持。
如 2014 年 4 月份发布的 14.04 LTS，Canonical 公司和社区支持到 2019 年。
类似的，Node.js，Linux kernel，Firefox 也采用类似的发布方式。

Java 未来的发布周期，将每半年发布一个大版本，每个季度发布一个中间特性版本。
这样可以把一些关键特性尽早合并入 JDK 之中，快速得到开发者反馈，可以在一定程度上避免 Java 9 两次被迫推迟发布日期的尴尬。

下图为 2017 年 JavaOne 大会时，Oracle 公开的未来 Java 版本发布和支持周期图。

[](../images/jdk10/jdk-lts.png)

## Java 10 新特性

这次发布的 Java 10，新带来的特性并不多。

根据官网公开资料，共有 12 个 JEP(JDK Enhancement Proposal 特性加强提议)，带来以下加强功能：

- JEP286，var 局部变量类型推断。

- JEP296，将原来用 Mercurial 管理的众多 JDK 仓库代码，合并到一个仓库中，简化开发和管理过程。

- JEP304，统一的垃圾回收接口。

- JEP307，G1 垃圾回收器的并行完整垃圾回收，实现并行性来改善最坏情况下的延迟。

- JEP310，应用程序类数据 (AppCDS) 共享，通过跨进程共享通用类元数据来减少内存占用空间，和减少启动时间。

- JEP312，ThreadLocal 握手交互。在不进入到全局 JVM 安全点 (Safepoint) 的情况下，对线程执行回调。
优化可以只停止单个线程，而不是停全部线程或一个都不停。

- JEP313，移除 JDK 中附带的 javah 工具。可以使用 javac -h 代替。

- JEP314，使用附加的 Unicode 语言标记扩展。

- JEP317，能将堆内存占用分配给用户指定的备用内存设备。

- JEP317，使用 Graal 基于 Java 的编译器，可以预先把 Java 代码编译成本地代码来提升效能。

- JEP318，在 OpenJDK 中提供一组默认的根证书颁发机构证书。开源目前 Oracle 提供的的 Java SE 的根证书，
这样 OpenJDK 对开发人员使用起来更方便。

- JEP322，基于时间定义的发布版本，即上述提到的发布周期。版本号为\$FEATURE.\$INTERIM.\$UPDATE.\$PATCH，
分别是大版本，中间版本，升级包和补丁版本。


## 部分特性说明

#### 1. var 类型推断。

这个语言功能在其他一些语言 (C#、JavaScript) 和基于 JRE 的一些语言 (Scala 和 Kotlin) 中，早已被加入。

在 Java 语言很早就在考虑，早在 2016 年正式提交了 JEP286 提议。
后来举行了一次公开的开发者调查，获得最多建议的是采用类似 Scala 的方案，
“同时使用 val 和 var”，约占一半；第二多的是“只使用 var”，约占四分之一。
后来 Oracle 公司经过慎重考虑，采用了只使用 var 关键字的方案。

有了这个功能，开发者在写这样的代码时：

> ArrayList<String> myList = new ArrayList<String>()

可以省去前面的类型声明，而只需要

> var list = new ArrayList<String>()

编译器会自动推断出 list 变量的类型。对于链式表达式来说，也会很方便：

```java

var stream = blocks.stream();  
... 

 int maxWeight = stream.filter(b -> b.getColor() == BLUE)      
                       .mapToInt(Block::getWeight)                   
                       .max();
```

开发者无须声明并且 import 引入 Stream 类型，只用 stream 作为中间变量，用 var 关键字使得开发效率提升。

不过 var 的使用有众多限制，包括不能用于推断方法参数类型，只能用于局部变量，如方法块中，而不能用于类变量的声明，等等。

另外，我个人认为，对于开发者而言，变量类型明显的声明会提供更加全面的程序语言信息，对于理解并维护代码有很大的帮助。
一旦 var 被广泛运用，开发者阅读三方代码而没有 IDE 的支持下，会对程序的流程执行理解造成一定的障碍。
所以我建议尽量写清楚变量类型，程序的易读维护性有时更重要一些。

#### 2. 统一的 GC 接口

在 JDK10 的代码中，路径为 openjdk/src/hotspot/share/gc/，
各个 GC 实现共享依赖 shared 代码，GC 包括目前默认的 G1，也有经典的 Serial、Parallel、CMS 等 GC 实现。



#### 3. 应用程序类数据（AppCDS）共享

CDS 特性在原来的 bootstrap 类基础之上，扩展加入了应用类的 CDS(Application Class-Data Sharing) 支持。

其原理为：在启动时记录加载类的过程，写入到文本文件中，再次启动时直接读取此启动文本并加载。
设想如果应用环境没有大的变化，启动速度就会得到提升。

我们可以想像为类似于操作系统的休眠过程，合上电脑时把当前应用环境写入磁盘，再次使用时就可以快速恢复环境。

我在自己 PC 电脑上做以下应用启动实验。

首先部署 wildfly 12 应用服务器，采用 JDK10 预览版作为 Java 环境。
另外需要用到一个工具 [cl4cds](https://simonis.github.io/cl4cds/)，
作用是把加载类的日志记录，转换为 AppCDS 可以识别的格式。

##### A、安装好 wildfly 并部署一个应用，具有 Angularjs, rest, jpa 完整应用技术栈，预热后启动三次，并记录完成部署时间

分别为 6716ms, 6702ms, 6613ms，平均时间为 6677ms。

##### B、加入环境变量并启动，导出启动类日志

> export PREPEND_JAVA_OPTS="-Xlog:class+load=debug:file=/tmp/wildfly.classtrace"

##### C、使用 cl4cds 工具，生成 AppCDS 可以识别的 cls 格式

> /jdk-10/bin/java -cp src/classes/ io.simonis.cl4cds /tmp/wildfly.classtrace /tmp/wildfly.cls

打开文件可以看到内容为：

```java
java/lang/Object id: 0x0000000100000eb0

java/io/Serializable id: 0x0000000100001090

java/lang/Comparable id: 0x0000000100001268

java/lang/CharSequence id: 0x0000000100001440

......

org/hibernate/type/AssociationType id: 0x0000000100c61208 super: 0x0000000100000eb0 interfaces: 0x0000000100a00d10 source: /home/shihang/work/jboss/wildfly/dist/target/wildfly-12.0.0.Final/modules/system/layers/base/org/hibernate/main/hibernate-core-5.1.10.Final.jar

org/hibernate/type/AbstractType id: 0x0000000100c613e0 super: 0x0000000100000eb0 interfaces: 0x0000000100a00d10 source: /home/shihang/work/jboss/wildfly/dist/target/wildfly-12.0.0.Final/modules/system/layers/base/org/hibernate/main/hibernate-core-5.1.10.Final.jar

org/hibernate/type/AnyType id: 0x0000000100c61820 super: 0x0000000100c613e0 interfaces: 0x0000000100c61030 0x0000000100c61208 source: /home/shihang/work/jboss/wildfly/dist/target/wildfly-12.0.0.Final/modules/system/layers/base/org/hibernate/main/hibernate-core-5.1.10.Final.jar

....

```
这个文件用于标记类的加载信息。

##### D、使用环境变量启动 wildfly，模拟启动过程并导出 jsa 文件，就是记录了启动时类的信息。

> export PREPEND_JAVA_OPTS="-Xshare:dump -XX:+UseAppCDS -XX:SharedClassListFile=/tmp/wildfly.cls -XX:+UnlockDiagnosticVMOptions -XX:SharedArchiveFile=/tmp/wildfly.jsa"

查看产生的文件信息，jsa 文件有较大的体积。

```java

/opt/work/cl4cds$ ls -l /tmp/wildfly.*

-rw-rw-r-- 1 shihang shihang   8413843 Mar 20 11:07 /tmp/wildfly.classtrace

-rw-rw-r-- 1 shihang shihang   4132654 Mar 20 11:11 /tmp/wildfly.cls

-r--r--r-- 1 shihang shihang 177659904 Mar 20 11:13 /tmp/wildfly.jsa

```

##### E、使用 jsa 文件启动应用服务器

> export PREPEND_JAVA_OPTS="-Xshare:on -XX:+UseAppCDS -XX:+UnlockDiagnosticVMOptions -XX:SharedArchiveFile=/tmp/wildfly.jsa"

启动完毕后记录时长，三次分别是 5535ms, 5333ms, 5225ms，平均为 5364ms，相比之前的 6677ms 可以算出启动时间提升了 20% 左右。

这个效率提升，对于云端应用部署很有价值。

以上实验方法[参考于技术博客](https://marschall.github.io/2018/02/18/wildfly-appcds.html)。

#### 4. JEP314，使用附加的 Unicode 语言标记扩展。

JDK10 对于 Unicode BCP 47 有了更多的支持，BCP 47 是 IETF 定义语言集的规范文档。使用扩展标记，可以更方便的获得所需要的语言地域环境。

如 JDK10 加入的一个方法，

> java.time.format.DateTimeFormatter::localizedBy

通过这个方法，可以采用某种数字样式，区域定义或者时区来获得时间信息所需的语言地域本地环境信息。

附：从[链接](https://gunnarmorling.github.io/jdk-api-diff/jdk9-jdk10-api-diff.html#java.time.format.DateTimeFormatter)
可以看到 JDK10 所有的方法级别改动。

#### 5. 查看当前 JDK 管理根证书。

自 JDK9 起在 keytool 中加入参数 -cacerts，可以查看当前 JDK 管理的根证书。
而 OpenJDK9 中 cacerts 为空，这样就会给开发者带来很多不变。

EP318 就是利用 Oracle 开源出 Oracle JavaSE 中的 cacerts 信息，
在 OpenJDK 中提供一组默认的根证书颁发机构证书，目前有 80 条记录。

```java

/jdk-10/bin$ ./keytool -list -cacerts

Enter keystore password:   Keystore type: JKS

Keystore provider: SUN



Your keystore contains 80 entries



verisignclass2g2ca [jdk], Dec 2, 2017, trustedCertEntry, 

Certificate fingerprint (SHA-256): 3A:43:E2:20:FE:7F:3E:A9:65:3D:1E:21:74:2E:AC:2B:75:C2:0F:D8:98:03:05:BC:50:2C:AF:8C:2D:9B:41:A1

......

```

### 下一版本展望

下一个 Java 大版本会是 Java 11，也是 Java 8 之后的 LTS 版本，预计会在今年的 9 月份发布。目前只有四个 JEP，更多加强提议会逐步加入。

这个版本会充分发挥模块化的能力，把当前 JDK 中的关于 JavaEE 和 Corba 的部分移除，变得更加紧凑。

虽然 JDK9 最大的亮点是模块化，但 Java 业界广泛接纳并且适应需要一个过程。当前已经有一些支持模块化的类库，如 log4j2，但大多数还未支持。

可以预见 JDK11 发布之后，模块化特性就成为长期支持特性，会有越来越多的类库提供对模块化的支持。

Java 依然会是最适合应用开发的语言和平台，庞大的社区和广泛的开发者，会不断促使 Java 不断完善优化，在各个编程领域继续发扬光大。

对文中引用文章原作者表示致谢！引用的图示，数据和方法都属于原作者。
下一个 Java 大版本会是 Java 11，也是 Java 8 之后的 LTS 版本，
预计会在今年的 9 月份发布。目前只有四个 JEP，更多加强提议会逐步加入。
这个版本会充分发挥模块化的能力，把当前 JDK 中的关于 JavaEE 和 Corba 的部分移除，变得更加紧凑。
虽然 JDK9 最大的亮点是模块化，但 Java 业界广泛接纳并且适应需要一个过程。当前已经有一些支持模块化的类库，
如 log4j2，但大多数还未支持。 
可以预见 JDK11 发布之后，模块化特性就成为长期支持特性，会有越来越多的类库提供对模块化的支持。 
Java 依然会是最适合应用开发的语言和平台，庞大的社区和广泛的开发者，会不断促使 Java 不断完善优化，在各个编程领域继续发扬光大。
对文中引用文章原作者表示致谢！引用的图示，数据和方法都属于原作者。
 













