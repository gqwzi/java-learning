
## [原文](https://www.jianshu.com/p/57bffc6e7acd)

## [原文](https://blog.csdn.net/raintungli/article/details/54910152)


# 由浅入深学习java8的Lambda原理

> java8lambda由浅入深，通过一个简单例子，逐步深入了解lambda实现原理。

## 一个简单的java8中lambda例子
先看一个简单的使用lambda的例子，我们从这个例子开始逐步探索java8中lambda是如何实现的。
```java

/**
 * Created by qiyan on 2017/4/16.
 */
public class LambdaTest {

    public static void main(String[] args) {
        Func add = (x, y) -> x + y;
        System.out.println(add.exec(1, 2));
    }
}


@FunctionalInterface
interface Func {
    int exec(int x, int y);
}

```

上面源码编译完成后执行 javap -p -v -c LambdaTest 查看反编译结果：

或者 javap -v -p Lambda.class 查看

注意  -p 这个参数 -p 参数会显示所有的方法，而不带默认是不会反编译private 的方法的

```java
Classfile /Users/qiyan/src/test/src/main/java/LambdaTest.class
  Last modified 2017-4-16; size 969 bytes
  MD5 checksum 0a1db458a90b20fbfae645b576725fd4
  Compiled from "LambdaTest.java"
public class LambdaTest
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #7.#18         // java/lang/Object."<init>":()V
   #2 = InvokeDynamic      #0:#23         // #0:exec:()LFunc;
   #3 = Fieldref           #24.#25        // java/lang/System.out:Ljava/io/PrintStream;
   #4 = InterfaceMethodref #26.#27        // Func.exec:(II)I
   #5 = Methodref          #28.#29        // java/io/PrintStream.println:(I)V
   #6 = Class              #30            // LambdaTest
   #7 = Class              #31            // java/lang/Object
   #8 = Utf8               <init>
   #9 = Utf8               ()V
  #10 = Utf8               Code
  #11 = Utf8               LineNumberTable
  #12 = Utf8               main
  #13 = Utf8               ([Ljava/lang/String;)V
  #14 = Utf8               lambda$main$0
  #15 = Utf8               (II)I
  #16 = Utf8               SourceFile
  #17 = Utf8               LambdaTest.java
  #18 = NameAndType        #8:#9          // "<init>":()V
  #19 = Utf8               BootstrapMethods
  #20 = MethodHandle       #6:#32         // invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
  #21 = MethodType         #15            //  (II)I
  #22 = MethodHandle       #6:#33         // invokestatic LambdaTest.lambda$main$0:(II)I
  #23 = NameAndType        #34:#35        // exec:()LFunc;
  #24 = Class              #36            // java/lang/System
  #25 = NameAndType        #37:#38        // out:Ljava/io/PrintStream;
  #26 = Class              #39            // Func
  #27 = NameAndType        #34:#15        // exec:(II)I
  #28 = Class              #40            // java/io/PrintStream
  #29 = NameAndType        #41:#42        // println:(I)V
  #30 = Utf8               LambdaTest
  #31 = Utf8               java/lang/Object
  #32 = Methodref          #43.#44        // java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
  #33 = Methodref          #6.#45         // LambdaTest.lambda$main$0:(II)I
  #34 = Utf8               exec
  #35 = Utf8               ()LFunc;
  #36 = Utf8               java/lang/System
  #37 = Utf8               out
  #38 = Utf8               Ljava/io/PrintStream;
  #39 = Utf8               Func
  #40 = Utf8               java/io/PrintStream
  #41 = Utf8               println
  #42 = Utf8               (I)V
  #43 = Class              #46            // java/lang/invoke/LambdaMetafactory
  #44 = NameAndType        #47:#51        // metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
  #45 = NameAndType        #14:#15        // lambda$main$0:(II)I
  #46 = Utf8               java/lang/invoke/LambdaMetafactory
  #47 = Utf8               metafactory
  #48 = Class              #53            // java/lang/invoke/MethodHandles$Lookup
  #49 = Utf8               Lookup
  #50 = Utf8               InnerClasses
  #51 = Utf8               (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
  #52 = Class              #54            // java/lang/invoke/MethodHandles
  #53 = Utf8               java/lang/invoke/MethodHandles$Lookup
  #54 = Utf8               java/lang/invoke/MethodHandles
{
  public LambdaTest();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 4: 0

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=4, locals=2, args_size=1
         0: invokedynamic #2,  0              // InvokeDynamic #0:exec:()LFunc;
         5: astore_1
         6: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
         9: aload_1
        10: iconst_1
        11: iconst_2
        12: invokeinterface #4,  3            // InterfaceMethod Func.exec:(II)I
        17: invokevirtual #5                  // Method java/io/PrintStream.println:(I)V
        20: return
      LineNumberTable:
        line 7: 0
        line 8: 6
        line 9: 20

  private static int lambda$main$0(int, int);
    descriptor: (II)I
    flags: ACC_PRIVATE, ACC_STATIC, ACC_SYNTHETIC
    Code:
      stack=2, locals=2, args_size=2
         0: iload_0
         1: iload_1
         2: iadd
         3: ireturn
      LineNumberTable:
        line 7: 0
}
SourceFile: "LambdaTest.java"
InnerClasses:
     public static final #49= #48 of #52; //Lookup=class java/lang/invoke/MethodHandles$Lookup of class java/lang/invoke/MethodHandles
BootstrapMethods:
  0: #20 invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #21 (II)I
      #22 invokestatic LambdaTest.lambda$main$0:(II)I
      #21 (II)I

```

根据反编译结果，不难看出lambda表达式：(x, y) -> x + y 被编译成了一个方法：lambda$main$0

```java
private static int lambda$main$0(int, int);
    descriptor: (II)I
    flags: ACC_PRIVATE, ACC_STATIC, ACC_SYNTHETIC
    Code:
      stack=2, locals=2, args_size=2
         0: iload_0
         1: iload_1
         2: iadd
         3: ireturn
      LineNumberTable:
        line 7: 0
        
```
翻译成java代码：
```java

private static int lambda$main$0(int x, int y){
    return x + y;
}

```
再看看main方法字节码：

```java
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=4, locals=2, args_size=1
         0: invokedynamic #2,  0              // InvokeDynamic #0:exec:()LFunc;
         5: astore_1
         6: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
         9: aload_1
        10: iconst_1
        11: iconst_2
        12: invokeinterface #4,  3            // InterfaceMethod Func.exec:(II)I
        17: invokevirtual #5                  // Method java/io/PrintStream.println:(I)V
        20: return
      LineNumberTable:
        line 7: 0
        line 8: 6
        line 9: 20
        
```
执行main步骤：

- 0：通过invokedynamic指令生成调用对象；

- 5：存入本地变量表；

- 6：加载java.lang.System.out静态方法；

- 9：将lambda表达式生成的对象加载入执行栈；

- 10：将int类型1加载入执行栈；

- 11：将int类型2加载入执行栈；

- 12：执行lambda表达式生成的对象的exec方法；

- 17：输出执行结果；

lambda的要点就在invokedynamic这个指令了，通过invokedynamic指令生成目标对象，
接下来我们了解一下invokedynamic指令。

## invokedynamic指令
下面重点看看invokedynamic指令，首先我们来看看常量池中出现的的InvokeDynamic类型：

官方文档：<https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.10>

```java

CONSTANT_InvokeDynamic_info {
    u1 tag;//InvokeDynamic类型标记18
    u2 bootstrap_method_attr_index; //BootstrapMethods_attribute中的坐标
    u2 name_and_type_index; //名字&类型常量池坐标
}

```
接下来看看BootstrapMethods_attribute，
InvokeDynamic中的bootstrap_method_attr_index就是指向其中bootstrap_methods的下标：
官方文档：<https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.21>


```java
BootstrapMethods_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 num_bootstrap_methods;
    {   u2 bootstrap_method_ref;//方法引用
        u2 num_bootstrap_arguments;//参数数量
        u2 bootstrap_arguments[num_bootstrap_arguments];//参数
    } bootstrap_methods[num_bootstrap_methods];
}
```

继续bootstrap_methods中的bootstrap_method_ref：

官方文档：<https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.8>

```java

CONSTANT_MethodHandle_info {
    u1 tag;//MethodHandle类型标记15
    u1 reference_kind;//方法引用类型getfield／getstatic／putfield／putstatic／invokevirtual／invokestatic／invokespecial／new／invokeinterface，此例中使用的invokestatic。
    u2 reference_index;//引用类型，根据reference_kind确认，例如本例中kind为方法调用，所以index为Methodref，细节可以查看官方文档。
}

```

## 回归lambda例子
对invokedynamic学习总结一下：

invokedynamic指令通过找到BootstrapMethods中的方法，生成动态调用点，
对于本例，我们对照BootstrapMethods中的bootstrap_methods分析实现：

```java
BootstrapMethods:
  0: #20 invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #21 (II)I
      #22 invokestatic LambdaTest.lambda$main$0:(II)I
      #21 (II)I

```
按照上面指令，可以看出，invokedynamic指令通过java.lang.invoke.LambdaMetafactory#metafactory方法生成目标对象。

```java

public static CallSite metafactory(MethodHandles.Lookup caller,String invokedName,MethodType invokedType,MethodType samMethodType,MethodHandle implMethod,MethodType instantiatedMethodType) throws LambdaConversionException {

    AbstractValidatingLambdaMetafactory mf;

    mf = new InnerClassLambdaMetafactory(caller,invokedType,invokedName,samMethodType,implMethod,instantiatedMethodType,false,EMPTY_CLASS_ARRAY,EMPTY_MT_ARRAY);

    mf.validateMetafactoryArgs();

    return mf.buildCallSite();
}

```
查看相关代码可以看出，metafactory就是核心方法，
该方法通过InnerClassLambdaMetafactory类生成对象，
供后续调用，在InnerClassLambdaMetafactory源码中可以看到，
有提供开关是否dump生成的class文件。

```java
private static final ProxyClassesDumper dumper;

static {
    final String key = "jdk.internal.lambda.dumpProxyClasses";
    String path = AccessController.doPrivileged(new GetPropertyAction(key), null,new PropertyPermission(key , "read"));
    dumper = (null == path) ? null : ProxyClassesDumper.getInstance(path);
}

```
//dump逻辑

```java
if (dumper != null) {
  AccessController.doPrivileged(new PrivilegedAction<Void>() {
    @Override
    public Void run() {
    dumper.dumpClass(lambdaClassName, classBytes);
    return null;
    }
  }, null,
    new FilePermission("<<ALL FILES>>", "read, write"),
    // createDirectories may need it
    new PropertyPermission("user.dir", "read"));
}

```
执行下面命令生成中间对象java -Djdk.internal.lambda.dumpProxyClasses LambdaTest


```java
Classfile /Users/qiyan/src/test/src/main/java/LambdaTest$$Lambda$1.class
  Last modified 2017-4-17; size 236 bytes
  MD5 checksum 983fa2b5e7d29c46d6f885925909b83e
final class LambdaTest$$Lambda$1 implements Func
  minor version: 0
  major version: 52
  flags: ACC_FINAL, ACC_SUPER, ACC_SYNTHETIC
Constant pool:
   #1 = Utf8               LambdaTest$$Lambda$1
   #2 = Class              #1             // LambdaTest$$Lambda$1
   #3 = Utf8               java/lang/Object
   #4 = Class              #3             // java/lang/Object
   #5 = Utf8               Func
   #6 = Class              #5             // Func
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = NameAndType        #7:#8          // "<init>":()V
  #10 = Methodref          #4.#9          // java/lang/Object."<init>":()V
  #11 = Utf8               exec
  #12 = Utf8               (II)I
  #13 = Utf8               LambdaTest
  #14 = Class              #13            // LambdaTest
  #15 = Utf8               lambda$main$0
  #16 = NameAndType        #15:#12        // lambda$main$0:(II)I
  #17 = Methodref          #14.#16        // LambdaTest.lambda$main$0:(II)I
  #18 = Utf8               Code
{
  private LambdaTest$$Lambda$1();
    descriptor: ()V
    flags: ACC_PRIVATE
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #10                 // Method java/lang/Object."<init>":()V
         4: return

  public int exec(int, int);
    descriptor: (II)I
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=3, args_size=3
         0: iload_1
         1: iload_2
         2: invokestatic  #17                 // Method LambdaTest.lambda$main$0:(II)I
         5: ireturn
}

```
第一个例子学习分析到此结束，下面是根据原理，翻译的等价的最终执行代码。


```java
public class LambdaTest {

    public static void main(String[] args) {
        Func add = new LambdaTest$$Lambda$1();
        System.out.println(add.exec(1, 2));
    }

    private static int lambda$main$0(int x, int y) {
        return x + y;
    }

    static final class LambdaTest$$Lambda$1 implements Func {
        private LambdaTest$$Lambda$1() {
        }

        public int exec(int x, int y) {
            return LambdaTest.lambda$main$0(x, y);
        }
    }
}


@FunctionalInterface
interface Func {
    int exec(int x, int y);
}

```
## 总结
这样就完成的实现了Lambda表达式，使用invokedynamic指令，运行时调用LambdaMetafactory.metafactory动态的生成内部类，
实现了接口，内部类里的调用方法块并不是动态生成的，只是在原class里已经编译生成了一个静态的方法，内部类只需要调用该静态方法
