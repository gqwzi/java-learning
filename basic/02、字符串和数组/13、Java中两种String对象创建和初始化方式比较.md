
## [原文](http://henry-cong.iteye.com/blog/1163280)

# Java中两种String对象创建和初始化方式比较

在Java中，初始化String分为两种：
```java

1. String s1 = "11";
2. String s2 = new String("11")

```
## 区别：
- 方法1中，先在内存中查找有没有"11"这个字符串对象存在，如果存在就把s1指向这个字符串对象；

- 方法2中，不论内存中是否已经存在"11"这个字符串对象，都会新建一个对象。

 

前者会在栈中创建一个对象引用变量str，然后查看栈中是否存在“11”，如果没有，则将“11”存放进栈，
并令引用变量str指向它；如果已经有“11”，则直接令str指向它；后者是java中标准的对象创建方式，
其创建的对象将直接放置到堆中，每调用一次就会创建一个新的对象。这样充分利用了栈的数据共享优点，
当然也可能是一个陷阱，对象很有可能没有创建，只不过指向一个先前已经创建的对象；
而new()方法则能保证每次都创建一个新的对象。

下述代码展示了二者的不同：
```java
public   static   void  main(String[] args)  {
         String strA  =   " abc " ;
         String strB  =   " abc " ;
         String strAA  =   new  String( " abc " );
         String strBB  =   new  String( " abc " );
         System.out.println(strA  ==  strB);
         System.out.println(strAA  ==  strBB);
     } 
```
      
输出结果
``` 
true
false
```  

## 总结：

### java堆与栈 java String分配内存空间（详解）

栈内存 | 堆内存
|---|---
基础类型，对象引用（ 堆内存地址 ） | 由new 创建的对象和数组，
存取速度快 | 相对于栈内存较慢
数据大小声明周期必须确定 | 分配的内存由java 虚拟机自动垃圾回收器管理。动态分配内存大小
共享特性 | 栈中如果有字符串，则直接引用
如果没有，开辟新的空间存入值 | 每new 一次在堆内存中生成一个新的对象。
创建之后值可以改变 | String 类声明后则不可改变    

### 一、栈内存

基础类型 int, short, long, byte, float, double, boolean, char 和对象引用

 

### 栈的共享特性
```java
String str1 = "abc"; 
String str2 = "abc"; 
System.out.println(str1==str2); //true

```

1 、编译器先处理String str1 = "abc" ；它会在栈中创建一个变量为str1 的引用，
然后查找栈中是否有abc 这个值，如果没找到，就将abc 存放进来，然后将str1 指向abc 。

2 、   接着处理String str2 = "abc"; 在创建完b 的引用变量后，
因为在栈中已经有abc 这个值，便将str2 直接指向abc 。
这样，就出现了str1 与str2 同时均指向abc 的情况。

### 二、堆内存

new 、newarray 、anewarray 和multianewarray 等指令建立

   要注意: 我们在使用诸如String str = "abc" ；
   的格式定义类时，总是想当然地认为，
   创建了String 类的对象str 。担心陷阱！对象可能并没有被创建！
   而可能只是指向一个先前已经创建的 对象。只有通过new() 方法才能保证每次都创建一个新的对象。
    由于String 类的immutable 性质，当String 变量需要经常变换其值时，
    应该考虑使用StringBuffer 类，以提高程序效率。

### 三、  ==   内存地址比对

```java
String str1 = "abc"; 
String str2 = "abc"; 
System.out.println(str1==str2); //true    str1 和str2 同时指向 栈内存 中同一个内存空间

String str3 = "abc"; 
String str4 = new String("abc") ;

System.out.println(str3 == str4);    //flase str3 值在栈内存中，str4 值在堆内存中

 

String hello = "hello" ;

String hel = "hel" ;

String lo = "lo" ;

System.out.println(hello == "hel" + "lo") ; //true

// 两个常量相加，先检测栈内存中是否有hello 如有有，指向已有的栈中的hello 空间

System.out.println(hello == "hel" + lo) ;   //flase

System.out.println(hello == hel + lo) ;     //flase

 //lo 是在常量池中，不检查栈内存，在堆中产生一个新的hello

```
 

### 四、  equals  值进行比对

```java
 public boolean equals (Object anObject)
 
```

将此字符串与指定的对象比较。当且仅当该参数不为 null ，
并且是与此对象表示相同字符序列的 String 对象时，结果才为 true 。

```java
 String str5 = "abc"; 
String str6 = new String("abc") ;

System.out.println(str5.equals(str6));    //true   str5 的值str6 的值比对

```


###  五、  intern    栈中值的内存地址

 

```java
Public String intern()

```
当调用 intern 方法时

1 、如果池已经包含一个等于此 String 对象的字符串（用equals(Object) 方法确定），
则返回池中的字符串。

2 、将此 String 对象添加到池中，并返回此 String 对象的引用。

 

```
String s7 = new String("abc") ;

String s8 = "abc" ;

 

System.out.println(s7 == s7.intern()) ;//flase ；

System.out.println(s8 == s7.intern() );//true

 
```

1. 检查栈内存中有没有abc 对象如果有

2. 将s7 指向pool 中abc
 
 
[Java String 创建对象](https://blog.csdn.net/chy555chy/article/details/52795984)