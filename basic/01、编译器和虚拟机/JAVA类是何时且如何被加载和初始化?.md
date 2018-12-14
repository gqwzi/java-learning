
# [原文 When and how a Java class is loaded and initialized?](https://www.programcreek.com/2013/01/when-and-how-a-java-class-is-loaded-and-initialized/)

# JAVA 类是如何且何时被加载和初始化


在Java中，首先编写.java文件，然后在编译期间将其编译为.class文件。 
Java在运行时能够加载类。

混淆是“加载”和“初始化”之间的区别。
何时以及如何加载和初始化Java类？
通过使用下面的简单示例可以清楚地说明


## 说加载一个类是什么意思？

首先将C / C ++编译为本机机器代码，然后在编译后需要链接步骤。
链接的作用是组合来自不同位置的源文件并形成可执行程序。

Java不这样做。 Java的链接式步骤在加载到JVM中时完成。


不同的JVM以不同的方式加载类，但基本规则只是在需要时才加载类。
如果加载的类需要其他类，则也会加载它们。加载过程是递归的。



## 何时以及如何加载Java类？

在Java中，加载策略由ClassLoader处理。
以下示例显示如何以及何时为简单程序加载类。

TestLoader.java
```java
package space.pankui.basic.jvm;
 
public class TestLoader {
    public static void main(String[] args) {
        System.out.println("test");
    }
}

```

```java
package space.pankui.basic.jvm;

public class A {
    public void method(){
        System.out.println("inside of A");
    }
}

```
这是IDEA 中的目录层次结构
```
|-src 
   |-mail
     |-java 
       |-space
          |-pankui
             |-basic
                |-jvm
                   |-TestLoader.java
                   |-A.java
```

通过运行以下命令，我们可以获取有关每个加载的类的信息。 
“-verbose：class”选项显示有关每个已加载类的信息。

> java -verbose space.pankui.basic.jvm.TestLoader


部分输出:

```java
Loaded java.security.UnresolvedPermission from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.security.BasicPermissionCollection from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded space.pankui.basic.jvm.TestLoader from file:/study-notes/java/out/production/classes/]
[Loaded sun.launcher.LauncherHelper$FXHelper from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.lang.Class$MethodArray from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.lang.Void from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
test
[Loaded java.lang.Shutdown from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.lang.Shutdown$Lock from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]

```


现在如果我们将TestLoader.java更改为：
```java
public class TestLoader {
	public static void main(String[] args) {
		System.out.println("test");
		A a = new A();
		a.method();
	}
}
```

并再次运行相同的命令，输出将是

```java
Loaded java.security.UnresolvedPermission from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.security.BasicPermissionCollection from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded space.pankui.basic.jvm.TestLoader from file:/study-notes/java/out/production/classes/]
[Loaded sun.launcher.LauncherHelper$FXHelper from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.lang.Class$MethodArray from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.lang.Void from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
test
[Loaded space.pankui.basic.jvm.A from file:/study-notes/java/out/production/classes/]
inside of A
[Loaded java.lang.Shutdown from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
[Loaded java.lang.Shutdown$Lock from /jdk1.8.0_162.jdk/Contents/Home/jre/lib/rt.jar]
```

我们可以看到以红色突出显示的差异。 A.class仅在使用时加载。总之，加载了一个类

- 何时执行新的字节码。例如，SomeClass f = new SomeClass（）; 

- 当字节码对类进行静态引用时。例如，System.out。

## 何时以及如何初始化Java类？

- 首次使用类中的符号时，将初始化类。加载类时，它不会被初始化。

- JVM将以文本顺序初始化超类和字段，首先初始化静态，最终字段，并在初始化之前为每个字段赋予默认值。

- Java类实例初始化是一个示例，显示字段，静态字段和构造函数的执行顺序。

References:
1. [Java class loader](https://www.javaworld.com/article/2077260/learn-java/learn-java-the-basics-of-java-class-loaders.html)
2. [Java class loading](https://www.ibm.com/developerworks/library/j-dyn0429/)
3. [Class and object initialization](https://www.javaworld.com/article/3040564/learn-java/java-101-class-and-object-initialization-in-java.html)

















