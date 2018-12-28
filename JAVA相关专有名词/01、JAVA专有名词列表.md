

#  JAVA专有名词列表


## JDK
JDK就是Java DevelopmentKit.简单的说JDK是面向开发人员使用的SDK，它提供了Java的开发环境和运行环境。

 
## JRE
JRE是Java Runtime Enviroment是指Java的运行环境，是面向Java程序的使用者，而不是开发者。

## SDK
SDK是Software Development Kit 一般指软件开发包，可以包括函数库、编译程序等。 

## TM
看官方文档的话一定会经常看到TM，比如

java -version
```java
java version "1.8.0_162"
Java(TM) SE Runtime Environment (build 1.8.0_162-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.162-b12, mixed mode)

```

- 解释   
TM是英文Trademark商标的意思。 

在我国，商标符号是：® 或 注 ，没有使用TM的规定，采用“先注册原则”，所以如果谁注册了某个东西为商标就可以加个商标符号。 

在美国，商标采用“先使用原则”，如果产生冲突，法律（法庭）会保护优先使用者权利即使没有注册。

美国一般习惯使用TM来表明：“这是一个我们已经使用的商标”
 

## [JCP](https://zh.wikipedia.org/wiki/JCP)

JCP（Java Community Process）成立于1998年，java社区进程 是使有兴趣的各方参与定义Java的特征和未来版本的正式过程。

Java Community Process是一个由oracle（曾经是sun）领导的，负责管理java和接受各种Java Specification Requests的组织，
这个组织很多大厂（例如谷歌，IBM等）都加入了

JCP使用JSR（Java规范请求，Java Specification Requests）作为正式规范文档，
描述被提议加入到Java体系中的的规范和技术。

JSR变为final状态前需要正式的公开审查，并由JCP Executive Committee投票决定。
最终的JSR会提供一个参考实现，它是免费而且公开源代码的；还有一个验证是否符合API规范的Technology Compatibility Kit。



## JSR

Java Specification Requests，Java规范请求，
由JCP成员向委员会提交的Java发展议案，经过一系列流程后，如果通过最终会体现在未来的Java中

Java Specification Request是java的spec（规范），
在没有正式确定某版本之前会存在很多Java Specification Requests，
最终JSR会由加入JCP的那些大佬们投票决定，例如lambda在JSR335的相关讨论

## TCK
Technology Compatibility Kit，技术兼容性测试 
如果一个平台型程序想要宣称自己兼容Java，就必须通过TCK测试


## [RFC](https://zh.wikipedia.org/wiki/RFC)

请求意见稿（英语：Request For Comments，缩写：RFC），是由互联网工程任务组（IETF）发布的一系列备忘录。
文件收集了有关互联网相关信息，以及UNIX和互联网社群的软件文件，以编号排定。
当前RFC文件是由互联网协会（ISOC）赞助发行。


## OpenJDK
Sun公司初始设立的开发Java源码组织，是组织也是开源JDK的名字

## JME
   JME(Java Monkey Engine)是一个高性能的3D图形API，采用LWJGL作为底层支持。它的后续版本将支持JOGL。
   JME和Java 3D具有类似的场景结构，开发者必须以树状方式组织自己的场景。
   JME有一套很好的优化机制，这使得它得运行速度要比Java 3D快很多。
 
## J2ME
   Java 2 Micro Edition，从Java 5.0开始改名为Java ME。
   
## J2SE
   Java 2 Standard Edition，从Java 5.0开始改名为Java SE。
   
##  J2EE
   Java 2 Enterprise Edition，从Java 5.0开始改名为Java EE。
   
## JMX
   JMX（Java Management Extensions）是一个为应用程序植入管理功能的框架。
 
 