

Java变量的初始化顺序为：静态变量或静态语句块–>实例变量或初始化语句块–>构造方法–>@Autowired


# ”java程序设计语言总是采用值调用。也就是说，方法得到的是所有参数值的一个拷贝，特别是，方法不能修改传递给它的任何参数变量的内容。“


```java
 
 第一个例子：基本类型
 void foo(int value) {
     value = 100;
 }
 foo(num); // num 没有被改变
 
 第二个例子：没有提供改变自身方法的引用类型
 void foo(String text) {
     text = "windows";
 }
 foo(str); // str 也没有被改变
 
 第三个例子：提供了改变自身方法的引用类型
 StringBuilder sb = new StringBuilder("iphone");
 void foo(StringBuilder builder) {
     builder.append("4");
 }
 foo(sb); // sb 被改变了，变成了"iphone4"。
 
 第四个例子：提供了改变自身方法的引用类型，但是不使用，而是使用赋值运算符。
 StringBuilder sb = new StringBuilder("iphone");
 void foo(StringBuilder builder) {
     builder = new StringBuilder("ipad");
 }
 foo(sb); // sb 没有被改变，还是 "iphone"。


```


str += "123" 实际上是 str = str + "123", 实际上是 str 指向一个新生成的字符串对象。而原来的字符串并没有改变。
(str 拼接都会新建一个对象，而原来对象的不变【可以查看字节码】)

但是 StringBuilder 的 引用  builder.append("4"); 是在原来对象上操作。所以会改变。
但是如果新建一个对象则不会

String 有点特殊。。。它不是基本类型。。
但string 做str+="is a";的时候。。是另外创建一个地址。。所以。。原地址的数据是不会变的。。
如果你想用地址的话。。用StringBuilder。。。这也就是为什么很多博客推荐用StringBuilder 来做字符串的增加。。
因为节省内存地址.

函数 做 参数传递的时候1.基本类型做的是值传递2.引用类型做的是地址传递所以第一个是传递地址。
所以 地址本身上数据的改变也影响原先指向这个地址的数据.

  
 
 
从局部变量/方法参数开始讲起：局部变量和方法参数在jvm中的储存方法是相同的，
都是在栈上开辟空间来储存的，随着进入方法开辟，退出方法回收。
以32位JVM为例，boolean/byte/short/char/int/float以及引用都是分配4字节空间，long/double分配8字节空间。
对于每个方法来说，最多占用多少空间是一定的，这在编译时就可以计算好。
我们都知道JVM内存模型中有，stack和heap的存在，但是更准确的说，是每个线程都分配一个独享的stack，所有线程共享一个heap。
对于每个方法的局部变量来说，是绝对无法被其他方法，甚至其他线程的同一方法所访问到的，更遑论修改。
当我们在方法中声明一个 int i = 0，或者 Object obj = null 时，仅仅涉及stack，不影响到heap，
当我们 new Object() 时，会在heap中开辟一段内存并初始化Object对象。
当我们将这个对象赋予obj变量时，仅仅是stack中代表obj的那4个字节变更为这个对象的地址。
数组类型引用和对象：当我们声明一个数组时，如int[] arr = new int[10]，因为数组也是对象，
arr实际上是引用，stack上仅仅占用4字节空间，new int[10]会在heap中开辟一个数组对象，然后arr指向它。
当我们声明一个二维数组时，如 int[][] arr2 = new int[2][4]，arr2同样仅在stack中占用4个字节，
会在内存中开辟一个长度为2的，类型为int[]的数组，然后arr2指向这个数组。这个数组内部有两个引用（大小为4字节），
分别指向两个长度为4的类型为int的数组。

[原文](https://www.zhihu.com/question/31203609)





 