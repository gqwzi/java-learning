
# [Java SE 7 Features and Enhancements](http://www.oracle.com/technetwork/java/javase/jdk7-relnotes-418459.html)


## Java 7 新特性及更新内容详情如下：

### Java 编程语言特性

- 二进制形式的字面值表示
- 在数值类型的字面值中使用下划线分隔符联接
- 创建泛型实例时自动类型推断
- switch-case语句支持字符串类型
- 新增try-with-resources语句
- 单个catch子句同时捕获多种异常类型
- 改进使用带泛型可变参数的方法时的编译器警告和错误提示机制

### Swing

- 新增javax.swing.JLayer 类，一个灵活而且功能强大的Swing组件修饰器。
它使你能够直接利用组件和组件的事件响应而无需修改底层组件。你可以点击查看如何使用JLayer修饰组件。

- Nimbus Look & Feel (L&F) 从包com.sun.java.swing移动到标准的API包javax.swing;
详细信息请查看javax.swing.plaf.nimbus。 尽管它不是默认的L&F，但是现在你可以非常方便地使用它了。
你可以查看Java教程中Nimbus Look and Feel部分的详细信息，也可以在你的程序中运行三个使用Nimbus的简单方法的代码示例。

- 在以前的版本中, 在同一个容器中混合使用重量级的AWT组件和轻量级的Swing组件可能会引发某些问。
不过，现在你可以在Java SE 7完美地混合使用它们了。你可以点击这里查看相关文章。

- Java SE 7 支持以指定的透明度或非矩形的方式来显示窗体，
你可以点击这里查看Java教程中关于如何创建带有指定透明度和非矩形的窗体的部分内容。

- 类javax.swing.JColorChooser中新增了对以HSV方式来表现RGB色彩模型的支持。
HSV和HSL是两种最常见的用于表示RGB色彩模型的表示方式。
它们均表示色调、饱和度、亮度三个参数，不过取值形式不同。
HSL的三个参数的取值范围均为0-255，HSV的三个参数的取值范围分别为0°-360°、0.0-1.0、0.0-1.0。



### Java IO
- 包java.nio.file以及相关联的包java.nio.file.attribute提供对文件IO以及访问文件系统更全面的支持。
JDK7也支持zip压缩格式的文件系统。你可以参考以下资源获得更多信息：
 
  - 你可以点击查看Java教程中关于文件I/O(NIO 2.0特性)的部分内容；nio表示非阻塞式的IO(non-blocking I/O)。
  - 开发一个自定义的文件系统提供者。
  - zip压缩格式的文件系统提供者。
  - 目录%JAVA_HOME%/sample/nio/chatserver/下含有包括java.nio.file包在内的新API的演示示例。
  - 目录%JAVA_HOME%/demo/nio/zipfs/下含有NIO 2.0网络文件系统的演示示例。


### 网络
- 类java.net.URLClassLoader新增close方法，该方法可以有效解决如何支持从特定代码库，
尤其是jar文件中，加载类或资源的最新实现的问题。详情查看如何关闭URLClassLoader。

### 安全

- 新增ECDSA/ECDH等基于ECC加密算法的支持，详情查看Java加密体系结构中供应商SunEC提供支持的算法的部分内容。

- 禁用了MD2等一些弱加密算法，Java SE 7提供一种机制，用于在处理证书路径或与TLS交互时拒绝使用指定的加密算法。
详情查看Java公共密钥程序员指南中的附录D：禁用指定的加密算法和Java安全套接字扩展中的禁用加密算法。

- 对Java安全套接字扩展(Java Secure Socket Extension)中的SSL/TLS进行了一系列增强和完善。


### 并发

- 基于类java.util.concurrent.ForkJoinPool的fork/join框架，
作为接口java.util.concurrent.Executor的一个实现，
它被用来高效地运行工作线程池中的大量任务。其中还使用了一种名为work-stealing的技术，
它可以充分利用多处理器来保证所有的工作线程一直正常工作。详情查看Java教程中的Fork/Join部分。
目录%JAVA_HOME%/sample/forkjoin/中包含fork/join的演示示例。

- 新增java.util.concurrent.ThreadLocalRandom类，它消除了使用伪随机数的线程之间的竞争。
在多线程并发访问的情况下，使用ThreadLocalRandom比使用Math.random()可以减少并发线程之间的竞争，
从而获得更好的性能。例如：

> int r = ThreadLocalRandom.current().nextInt(4, 77);
//将返回一个4-77之间的随机整数(不包含77)。

- 新增java.util.concurrent.Phaser类，
它是一个新的类似于java.util.concurrent.CyclicBarrier的线程同步障碍辅助工具类(它允许一组线程互相等待，
直到到达某个公共屏障点)。


### Rich Internet Application(RIA)/部署
暂略。


### Java 2D
- 一个新的基于XRender的渲染管道能够提供改进的图形运行性能，以支持现在的基于DirectX 11的桌面应用。
默认情况下，这个渲染管道并未启用，不过你可以使用命令行设置属性-Dsun.java2d.xrender=true来启用它。

- 现在JDK可以通过诸如[GraphicsEnvironment.getAvailableFontFamilyNames](https://docs.oracle.com/javase/7/docs/api/java/awt/GraphicsEnvironment.html#getAvailableFontFamilyNames%28%29)等方法来枚举并显示系统中已安装的OpenType/CFF字体了，
并且这些字体都可以被方法Font.createFont识别。
你可以查看Java教程[选择指定的字体](https://docs.oracle.com/javase/tutorial/2d/text/fonts.html)。

- 类[java.awt.font.TextLayout](https://docs.oracle.com/javase/7/docs/api/java/awt/font/TextLayout.html)现在可以支持西藏文字的脚本了。

- 在Windows和Solaris操作系统中，文件fontconfig.properties中静态指定了JDK可以使用的逻辑字体。
不过，在多数Linux系统的实现中，并没有保证在特定的语言环境下对特定字体表现的支持。
在Java SE 7中，libfontconfig可选择在「未识别」的Linux平台上使用的逻辑字体。
更多信息可以查看[Fontconfig](https://www.freedesktop.org/wiki/Software/fontconfig/)。


### Java XML
- Java SE 7 现在已经更新Java API for XML Processing (JAXP)至1.4.5版本，与以前的版本相比，该版本修复了许多bug，
并且做了许多的改进，尤其是在一致性、安全性和性能方面。虽然JAXP仍然处于1.4版本，不过StAX已经升级到了1.2版本。
更多信息你可以查看JAXP 1.4.5发行说明以及JAXP 1.4.5更新日志。

- Java SE 7更新Java Architecture for XML Binding (JAXB)至2.2.3版本，详情查看2.2以上版本的JAXB更新日志。

- Java SE 7 更新 Java API for XML Web Services (JAX-WS)至2.2.4版本。详情查看2.2以上版本的JAX-WS更新日志。

### 国际化
- Java SE 7中添加或改进了对Unicode 6.0.0、本地化目录、本地化类文件以及ISO 4217货币符号扩展性的支持。

### java.lang包
- Java SE 7 修复了以前版本中多线程的自定义类加载器可能出现死锁的问题。

### Java虚拟机(JVM)
- java虚拟机支持非Java语言

- G1(Garbage-First)垃圾收集器,G1是一个服务器端的垃圾收集器用于替换 Concurrent Mark-Sweep Collector (CMS)

- Java HotSpot虚拟机性能增强


### Jdbc 4.1

- 支持使用 try-with-resources 语句进行自动的资源释放，包括连接、语句和结果集

- 支持 RowSet 1.1



--------

## 例子


#### 1.直接支持：二进制，八进制，十六进制表示

在java7里，整形(byte,short,int,long)类型的值可以用二进制类型来表示了，
在使用二进制的值时，需要在前面加上ob或oB表示二进制字面值的前缀0b。

比如以下b1、b2、b3三个变量的值相同：
    
```java
int a = 0b01111_00000_11111_00000_10101_01010_10;     // New
short b = (short)0b01100_00000_11111_0;               // New
byte c = (byte)0B0000_0001;                           // New
byte b1 = 0b00100001;     // New
byte b2 = 0x21;          // Old
byte b3 = 33;            // Old



/**
 * 二进制，八进制，十六进制表示
 */
int binaryNumber = 0b011;//这里表示2进制
System.out.println(binaryNumber);
int eightNumber = 022;//这里表示8进制，转换成十进制是18
System.out.println(eightNumber);
int sixTeenNumber = 0x14;//这里表示16进制，转换成十进制是20

```    


#### 2.数字变量对下滑线的支持

字面常量数字的下划线。用下划线连接整数提升其可读性，自身无含义，不可用在数字的起始和末尾。

Java编码语言对给数值型的字面值加下划线有严格的规定。

如上所述，你只能在数字之间用下划线。你不能用把一个数字用下划线开头，或者已下划线结尾。
这里有一些其它的不能在数值型字面值上用下划线的地方：

在数字的开始或结尾
对浮点型数字的小数点附件
F或L下标的前面
该数值型字面值是字符串类型的时候

```java
float pi1 = 3_.1415F; // 无效的; 不能在小数点之前有下划线
float pi2 = 3._1415F; // 无效的; 不能在小数点之后有下划线
long socialSecurityNumber1=999_99_9999_L;//无效的，不能在L下标之前加下划线
int a1 = _52; // 这是一个下划线开头的标识符，不是个数字
int a2 = 5_2; // 有效
int a3 = 52_; // 无效的，不能以下划线结尾
int a4 = 5_______2; // 有效的
int a5 = 0_x52; // 无效，不能在0x之间有下划线
int a6 = 0x_52; // 无效的，不能在数字开头有下划线
int a7 = 0x5_2; // 有效的 (16进制数字)
int a8 = 0x52_; // 无效的，不能以下划线结尾
int a9 = 0_52; // 有效的（8进制数）
int a10 = 05_2; // 有效的（8进制数）
int a11 = 052_; // 无效的，不能以下划线结尾

```

#### 3.switch对String的支持

之前就一直有一个打问号？为什么C#可以Java却不行呢？哈，
不过还有JDK1.7以后Java也可以了
例如：

```java
String status = "orderState";   
  switch (status) {  
    case "ordercancel":  
      System.out.println("订单取消");  
      break;  
    case "orderSuccess":  
      System.out.println("预订成功");  
      break;  
    default:  
      System.out.println("状态未知");  
  }

```

switch 语句比较表达式中的String对象和每个case标签关联的表达式，
就好像它是在使用String.equals方法一样;
因此，switch语句中 String对象的比较是大小写敏感的。
相比于链式的if-then-else语句，
Java编译器通常会从使用String对象的switch语句中生成更高效的字节码。

#### 4.try-with-resource
try-with-resources语句是一个声明一个或多个资源的try语句。
一个资源作为一个对象，必须在程序结束之后关闭。
try-with-resources语句确保在语句的最后每个资源都被关闭，
任何实现了Java.lang.AutoCloseable和java.io.Closeable的对象都可以使用try-with-resource来实现异常处理和关闭资源。

下面通过对比来体会这个新特性。
JDK1.7之前：

```java
/** 
 * JDK1.7之前我们必须在finally块中手动关闭资源，否则会导致资源的泄露 
 * @author Liao 
 * 
 */  
public class PreJDK7 {  

    public static String readFirstLingFromFile(String path) throws IOException {  
        BufferedReader br = null;  

        try {  
            br = new BufferedReader(new FileReader(path));  
            return br.readLine();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {
            //必须在这里关闭资源  
            if (br != null)  
                br.close();  
        }  
        return null;  
    }  
}

```
JDK1.7及以后版本

```java
/** 
 * JDK1.7之后就可以使用try-with-resources,不需要我们在finally块中手动关闭资源 .
 * @author Liao 
 */  
public class AboveJDK7 {  

    static String readFirstLineFromFile(String path) throws IOException {  

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {  
            return br.readLine();  
        }  
    }  
}

```


通过上面的对比，try-with-resources的优点

**代码精炼** ，在JDK1.7之前都有finally块，如果使用一些扩建可能会将finally块交由框架处理，如spring。
JDK及以后的版本只要资源类实现了AutoCloseable或Closeable程序在执行完try块后会自动close所使用的资源无论br.readLine()是否抛出异常，
我估计针对JDK1.7像Spring这些框架也会做出一些比较大的调整。

**代码更完全** ,在出现资源泄漏的程序中，很多情况是开发人员没有或者开发人员没有正确的关闭资源所导致的。
JDK1.7之后采用try-with-resources的方式，
则可以将资源关闭这种与业务实现没有很大直接关系的工作交给JVM完成，
省去了部分开发中可能出现的代码风险。

**异常抛出顺序**

在JDK1.7之前如果rd.readLine()与rd.close()都抛出异常则只会抛出finally块中的异常，
不会抛出rd.readLine()中的异常，这样经常会导致得到的异常信息不是调用程序想要得到的。

在JDK1.7及以后采用了try-with-resource机制，
如果在try-with-resource声明中抛出异常(如文件无法打开或无法关闭)的同时rd.readLine()也抛出异常，
则只会抛出rd.readLine()的异常。

**try-with-resource可以声明多个资源** 。
下面的例子是在一个ZIP文件中检索文件名并将检索后的文件存入一个txt文件中。

JDK1.7及以上版本：

```java
public class AboveJDK7_2 {  

    public static void writeToFileZipFileContents(String zipFileName,String outputFileName) throws java.io.IOException {  

        java.nio.charset.Charset charset = java.nio.charset.Charset.forName("US-ASCII");  

        java.nio.file.Path outputFilePath = java.nio.file.Paths.get(outputFileName);  

        //打开zip文件，创建输出流  
        try (  
                java.util.zip.ZipFile zf = new java.util.zip.ZipFile(zipFileName);  

                java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(outputFilePath, charset)  
            )   

            {//遍历文件写入txt  
                for (java.util.Enumeration entries = zf.entries(); entries.hasMoreElements();) {  

                        String newLine = System.getProperty("line.separator");  

                        String zipEntryName = ((java.util.zip.ZipEntry) entries.nextElement()).getName() + newLine;  
                        writer.write(zipEntryName, 0, zipEntryName.length());  
                }  
            }  
    }  
}

```
注：上面的例子，无论正常执行还是有异常抛出，zf和write都会被执行close()方法，
不过需要注意的是在JVM里调用的顺序是与生命的顺序相反。

在JVM中调用的顺讯为：
writer.close();
zf.close();
所以在使用时一定要注意资源关闭的顺序。


#### 5.在单个catch代码块中捕获多个异常，以及用升级版的类型检查重新抛出异常


在Java 7中，catch代码块得到了升级，用以在单个catch块中处理多个异常。

如果你要捕获多个异常并且它们包含相似的代码，使用这一特性将会减少代码重复度。
下面用一个例子来理解。

Java 7之前的版本：

```java
catch (IOException ex) {
    logger.error(ex);
    throw new MyException(ex.getMessage());
catch (SQLException ex) {
    logger.error(ex);
    throw new MyException(ex.getMessage());
}catch (Exception ex) {
    logger.error(ex);
    throw new MyException(ex.getMessage());
}

```

在Java 7中，我们可以用一个catch块捕获所有这些异常：

```java
catch(IOException | SQLException | Exception ex){
    logger.error(ex);
    throw new MyException(ex.getMessage());
}
```


如果用一个catch块处理多个异常，可以用管道符（|）将它们分开，
在这种情况下异常参数变量（ex）是定义为final的，所以不能被修改。
这一特性将生成更少的字节码并减少代码冗余。

另一个升级是编译器对重新抛出异常（rethrown exceptions）的处理。
这一特性允许在一个方法声明的throws从句中指定更多特定的异常类型。

与以前版本相比，Java SE 7 的编译器能够对再次抛出的异常(rethrown exception)做出更精确的分析。
这使得你可以在一个方法声明的throws从句中指定更具体的异常类型。

我们先来看下面的一个例子：

```java
static class FirstException extends Exception { }
static class SecondException extends Exception { }

public void rethrowException(String exceptionName) throws Exception {
    try {
        if (exceptionName.equals("First")) {
            throw new FirstException();
        } else {
            throw new SecondException();
        }
    } catch (Exception e) {
        throw e;
    }
}

```


这个例子中的try语句块可能会抛出FirstException或者SecondException类型的异常。
设想一下，你想在rethrowException方法声明的throws从句中指定这些异常类型。
在Java SE 7之前的版本，你无法做到。
因为在catch子句中的异常参数e是java.lang.Exception类型的，catch子句对外抛出异常参数e，
你只能在rethrowException方法声明的throws从句中指定抛出的异常类型为java.lang.Exception (或其父类java.lang.Throwable)。

不过，在Java SE 7中，你可以在rethrowException方法声明的throws从句中指定抛出的异常类型为FirstException和SecondException。
Java SE 7的编译器能够判定这个被throw语句抛出的异常参数e肯定是来自于try子句，
而try子句只会抛出FirstException或SecondException类型的异常。
尽管catch子句的异常参数e是java.lang.Exception类型，
但是编译器可以判断出它是FirstException或SecondException类型的一个实例：

```java
public class Java7MultipleExceptions {

    public static void main(String[] args) {
        try{
            rethrow("abc");
        }catch(FirstException | SecondException | ThirdException e){
            //以下赋值将会在编译期抛出异常，因为e是final型的
            //e = new Exception();
            System.out.println(e.getMessage());
        }
    }

    static void rethrow(String s) throws FirstException, SecondException, ThirdException {
        try {
            if (s.equals("First"))
                throw new FirstException("First");
            else if (s.equals("Second"))
                throw new SecondException("Second");
            else
                throw new ThirdException("Third");
        } catch (Exception e) {
            //下面的赋值没有启用重新抛出异常的类型检查功能，这是Java 7的新特性
            // e=new ThirdException();
            throw e;
        }
    }

    static class FirstException extends Exception {

        public FirstException(String msg) {
            super(msg);
        }
    }

    static class SecondException extends Exception {

        public SecondException(String msg) {
            super(msg);
        }
    }

    static class ThirdException extends Exception {

        public ThirdException(String msg) {
            super(msg);
        }
    }

}

```
不过，如果catch捕获的异常变量在catch子句中被重新赋值，那么异常类型检查的分析将不会启用，
因此在这种情况下，你不得不在方法声明的throws从句中指定异常类型为java.lang.Exception。

更具体地说，从Java SE 7开始，当你在单个catch子句中声明一种或多种类型的异常，
并且重新抛出这些被捕获的异常时，需符合下列条件，编译器才会对再次抛出的异常进行类型验证：

- try子句会抛出该异常。
- 在此之前，没有其他的catch子句捕获该异常。
- 该异常类型是catch子句捕获的多个异常中的一个异常类型的父类或子类。


#### 6.创建泛型时类型推断

```java

// 只要编译器可以从上下文中推断出类型参数，你就可以用一对空着的尖括号<>来代替泛型参数。
// 这对括号私下被称为菱形(diamond)。 在Java SE 7之前，你声明泛型对象时要这样
List<String> list = new ArrayList<String>(); 

//而在Java SE7以后，你可以这样 
List<String> list = new ArrayList<>(); 

// 因为编译器可以从前面(List)推断出推断出类型参数，所以后面的ArrayList之后可以不用写泛型参数了，只用一对空着的尖括号就行。
// 当然，你必须带着”菱形”<>，否则会有警告的。 
// Java SE7 只支持有限的类型推断：只有构造器的参数化类型在上下文中被显著的声明了，你才可以使用类型推断，否则不行。 
List<String> list = new ArrayList<>(); 
list.add("A"); 

//这个不行 
list.addAll(new ArrayList<>()); 

// 这个可以 
List<? extends String> list2 = new ArrayList<>(); 
list.addAll(list2);

```

#####  Java SE 7 新特性[没有对集合的增强支持] 下面新特性里没有下面这种赋值!!!

```java
//JDK1.7 这里赋值是不存在的,不存在!!!!!

<http://www.oracle.com/technetwork/java/javase/jdk7-relnotes-418459.html>

List<String> list = [item1,item2,item3];
String item1 = list[0];
String item2 = list[1];
String item3 = list[2];

Map<String,String> map = {key:value,key:value}
String mapValue = map[key];

```


#### 7.新增一些取环境信息的工具方法

```java
File System.getUserHomeDir() // 当前用户目录
File System.getUserDir() // 启动java进程时所在的目录
File System.getJavaIoTempDir() // IO临时文件夹
File System.getJavaHomeDir() // JRE的安装目录

```


#### 8.安全的加减乘除

```java
int Math.safeToInt(long value)
int Math.safeNegate(int value)
long Math.safeSubtract(long value1, int value2)
long Math.safeSubtract(long value1, long value2)
int Math.safeMultiply(int value1, int value2)
long Math.safeMultiply(long value1, int value2)
long Math.safeMultiply(long value1, long value2)
long Math.safeNegate(long value)
int Math.safeAdd(int value1, int value2)
long Math.safeAdd(long value1, int value2)
long Math.safeAdd(long value1, long value2)
int Math.safeSubtract(int value1, int value2)

```



### 9.ThreadLocalRandom并发生成随机数，线程安全

```java
ThreadLocalRandom localRandom = ThreadLocalRandom.current();
  localRandom.nextDouble();
  
```

#[转载](http://www.365mini.com/page/5.htm)


#[转载](https://www.jianshu.com/p/0d4a958b0f52)


# [翻译](https://www.oschina.net/news/20119/new-features-of-java-7#news_comments_wrapper)


















