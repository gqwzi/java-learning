
## [原文](http://www.cnblogs.com/WJ5888/p/4667086.html)

# Java 8 Lambda实现原理分析

> 下面代码是在JDK11 环境下执行

为了支持函数式编程，Java 8引入了Lambda表达式，那么在Java 8中到底是如何实现Lambda表达式的呢?
 Lambda表达式经过编译之后，到底会生成什么东西呢? 
 
 在没有深入分析前，让我们先想一想，Java 8中每一个Lambda表达式必须有一个函数式接口与之对应，
 函数式接口与普通接口的区别，可以[参考前面的内容]()，
 那么你或许在想Lambda表达式是不是转化成与之对应的函数式接口的一个实现类呢，
 然后通过多态的方式调用子类的实现呢，如下面代码是一个Lambda表达式的样例
 
```java
@FunctionalInterface
interface Print<T> {
    public void print(T x);
}
public class Lambda {   
    public static void PrintString(String s, Print<String> print) {
        print.print(s);
    }
    public static void main(String[] args) {
        PrintString("test", (x) -> System.out.println(x));
    }
}
    
```

按照上面的分析，理论上经过编译器处理后，最终生成的代码应该如下面所示：
```java
@FunctionalInterface
interface Print<T> {
    public void print(T x);
}

class Lambda$$0 implements Print<String> {
    @Override
    public void print(String x) {
        System.out.println(x);
    }
}

public class Lambda {
    public static void PrintString(String s, 
            Print<String> print) {
        print.print(s);
    }
    public static void main(String[] args) {
        PrintString("test", new Lambda$$0());
    }
}

```

再或者是一个内部类实现，代码如下所示：

```java
@FunctionalInterface
interface Print<T> {
    public void print(T x);
}
public class Lambda {   
    final class Lambda$$0 implements Print<String> {
        @Override
        public void print(String x) {
            System.out.println(x);
        }
    }  
    public static void PrintString(String s, 
            Print<String> print) {
        print.print(s);
    } 
    public static void main(String[] args) {
        PrintString("test", new Lambda().new Lambda$$0());
    }
}
```

异或是这种匿名内部类实现，代码如下所示:

```java
@FunctionalInterface
interface Print<T> {
    public void print(T x);
}
public class Lambda {   
    public static void PrintString(String s, 
            Print<String> print) {
        print.print(s);
    }
    public static void main(String[] args) {
        PrintString("test", new Print<String>() {
            @Override
            public void print(String x) {
                System.out.println(x);
            }
        });
    }
}
```
上面的代码，除了在代码长度上长了点外，与用Lambda表达式实现的代码运行结果是一样的，
那么Java 8到底是用什么方式实现的呢? 

是不是上面三种实现方式中的一种呢，你也许觉的自已想的是对的，
其实本来也就是对的，在Java 8中采用的是内部类来实现Lambda表达式

## 那么Lambda表达式到底是如何实现的呢？

为了探究Lambda表达式是如何实现的，就得需要研究Lambda表过式最终转化成的字节码文件，
这就需要jdk的bin目录下的一个字节码查看工具及反编译工具

> javap -p Lambda.class

上面命令中的-p表示输出所有类及成员，运行上面的命令后，得的结果如下所示:

```java
Compiled from "Lambda.java"
public class Lambda {
  public Lambda();
  public static void PrintString(java.lang.String, Print<java.lang.String>);
  public static void main(java.lang.String[]);
  private static void lambda$main$0(java.lang.String);
}
```

由上面的代码可以看出编译器会根据Lambda表达式生成一个私有的静态函数，注意，在这里说的是生成，而不是等价

```java
private static void lambda$main$0(java.lang.String);

```
为了验证上面的转化是否正确? 我们在代码中定义一个lambda$main$0 这个的函数，最终代码如下所示：

```java
@FunctionalInterface
interface Print<T> {
    public void print(T x);
}

public class Lambda {   
    public static void PrintString(String s, 
            Print<String> print) {
        print.print(s);
    }
   private static void lambda$main$0(String s) { 
        
   }
    public static void main(String[] args) {
        PrintString("test", (x) -> System.out.println(x));
    }
}
```

上面的代码在编译时会报错，因为存在两个lambda$main$0函数，如下所示，编译错误错误

```java
Error:(16, 25) java: error while generating class space.pankui.jdk8.Lambda
  (the symbol lambda$main$0(java.lang.String) conflicts with a compiler-synthesized symbol in space.pankui.jdk8.Lambda)
```
会发现lambda$main$0出现了两次，那么在代码编译的时候，就不知道去调用哪个，因此就会抛错。

有了上面的内容，可以知道的是Lambda表达式在Java 8中首先会生成一个私有的静态函数，
这个私有的静态函数干的就是Lambda表达式里面的内容,因此上面的代码初步可以转化成如下所示的代码

```java
@FunctionalInterface
interface Print<T> {
    public void print(T x);
}
public class Lambda {   
    public static void PrintString(String s, Print<String> print) {
        print.print(s);
    }
    
    private static void lambda$main$0(String x) {
        System.out.println(x);
    }
    
    public static void main(String[] args) {
        PrintString("test", /**lambda expression**/);
    }
}
```

转化成上面的形式之后，那么如何实现调用静态的lambda$0函数呢，
在这里可以在以下方法打上断点，可以发现在有lambda表达式的地方，运行时会进入这个函数

```java
//package java.lang.invoke  LambdaMetafactory 
    public static CallSite metafactory(MethodHandles.Lookup caller,
                                       String invokedName,
                                       MethodType invokedType,
                                       MethodType samMethodType,
                                       MethodHandle implMethod,
                                       MethodType instantiatedMethodType)
            throws LambdaConversionException {
        AbstractValidatingLambdaMetafactory mf;
        mf = new InnerClassLambdaMetafactory(caller, invokedType,
                                             invokedName, samMethodType,
                                             implMethod, instantiatedMethodType,
                                             false, EMPTY_CLASS_ARRAY, EMPTY_MT_ARRAY);
        mf.validateMetafactoryArgs();
        return mf.buildCallSite();
    }
```
在buildCallSite的函数中

```java
    // InnerClassLambdaMetafactory类
    @Override
    CallSite buildCallSite() throws LambdaConversionException {
        final Class<?> innerClass = spinInnerClass();
        // 其他省略        
}
        
```
函数spinInnerClass 构建了这个内部类，也就是生成了一个Lambda$$Lambda$1/716157500 这样的内部类,
这个类是在运行的时候构建的，并不会保存在磁盘中，如果想看到这个构建的类，可以通过设置环境参数
```java
System.setProperty("jdk.internal.lambda.dumpProxyClasses", ".");
```
会在你指定的路径 . 当前运行路径上生成这个内部类

在这个函数中可以发现为Lambda表达式生成了一个内部类，为了验证是否生成内部类，
可以在运行时加上-Djdk.internal.lambda.dumpProxyClasses，
加上这个参数后，运行时，会将生成的内部类class码输出到一个文件中

```java
final class Lambda$$Lambda$1 implements Print {
  private Lambda$$Lambda$1();
  public void print(java.lang.Object);
}
```

如果运行javap -c -p 则结果如下

```java
final class Lambda$$Lambda$1 implements Print {
  private Lambda$$Lambda$1();
    Code:
       0: aload_0
       1: invokespecial #10                 // Method java/lang/Object."<init>":()V
       4: return

  public void print(java.lang.Object);
    Code:
       0: aload_1
       1: checkcast     #14                 // class java/lang/String
       4: invokestatic  #20                 // Method Lambda.lambda$0:(Ljava/lang/String;)V
       7: return
}
```

通过上面的字节码指令可以发现实现上调用的是Lambda.lambda$main$0 这个私有的静态方法

因此最终的Lambda表达式等价于以下形式

```java
@FunctionalInterface
interface Print<T> {
    public void print(T x);
}
public class Lambda {   
    public static void PrintString(String s, Print<String> print) {
        print.print(s);
    }
    private static void lambda$main$0(String x) {
        System.out.println(x);
    }
    final class $Lambda$1 implements Print{
        @Override
        public void print(Object x) {
            lambda$main$0((String)x);
        }
    }
    public static void main(String[] args) {
        PrintString("test", new Lambda().new $Lambda$1());
    }
}
```

## 总结
这样就完成的实现了Lambda表达式，使用invokedynamic指令，运行时调用LambdaMetafactory.metafactory动态的生成内部类，
实现了接口，内部类里的调用方法块并不是动态生成的，只是在原class里已经编译生成了一个静态的方法，内部类只需要调用该静态方法











