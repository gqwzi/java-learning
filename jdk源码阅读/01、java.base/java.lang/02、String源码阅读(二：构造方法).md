
## [原文](https://juejin.im/post/5b6825bb6fb9a04f9963ca49)

## [原文2](https://www.jianshu.com/p/1d5065fd6675)

#  String源码阅读(二：构造方法)  

## 内部类

该内部类主要是提供排序的比较器，实现了Comparator接口和compare方法，
另外一个readResolve方法用于替换反序列化时的对象。
compare核心方法的逻辑是，根据两者编码是否相同做处理，如果相同则分 Latin1 或 UTF16 两种情况比较，
类似地，如果两者编码不同，则需要用 Latin1 编码与 UTF16 编码比较，而 UTF16 则要与 Latin1 比较。

```java
    private static class CaseInsensitiveComparator
            implements Comparator<String>, java.io.Serializable {
        // use serialVersionUID from JDK 1.2.2 for interoperability
        private static final long serialVersionUID = 8575799808933029326L;

        public int compare(String s1, String s2) {
            byte v1[] = s1.value;
            byte v2[] = s2.value;
            if (s1.coder() == s2.coder()) {
                return s1.isLatin1() ? StringLatin1.compareToCI(v1, v2)
                                     : StringUTF16.compareToCI(v1, v2);
            }
            return s1.isLatin1() ? StringLatin1.compareToCI_UTF16(v1, v2)
                                 : StringUTF16.compareToCI_Latin1(v1, v2);
        }

        /** Replaces the de-serialized object. */
        private Object readResolve() { return CASE_INSENSITIVE_ORDER; }
    }

```

## 构造方法

查阅String源码可以发现，它内部定义了很多构造方法，其中有些构造方法也都已经过时了，不建议使用，
这里的构造方法调用采用的是典型的门面模式，会有一两个主要的构造方法执行逻辑，
其余的构造方法都是调用这那些核心的构造方法来完成对象的构造，下面针对未过时的构造方法逐一进行分析。
       
### 无参构造

在我们new String()的时候，如果没有传入参数，那么就会调用这个构造方法，
这里它的value并不会直接赋值为null，而是采用空字符串的value值，这样保证了value的值永远不会为null，
value的长度可以为0，同样coder也是如此，正好符合前面所说的value字段上面添加的Stable注解的作用

```java
  public String() {
        this.value = "".value;
        this.coder = "".coder;
    }
```

### String(String original)
 
这个构造函数其实是一种拷贝，因为前面说过，String对象一旦创建，它是不可变的，
所以它创建的String对象是基于源字符串的一份拷贝，所以如果需要对字符串进行拷贝，
可以考虑使用该构造函数。

```java
   @HotSpotIntrinsicCandidate
    public String(String original) {
        this.value = original.value;
        this.coder = original.coder;
        this.hash = original.hash;
    }

```
另外，它使用了HotSpotIntrinsicCandidate注解，这个注解是HotSpot虚拟机特有的注解，
使用了该注解的方法，它表示该方法在HotSpot虚拟机内部可能会自己来编写内部实现，用以提高性能，
但是它并不是必须要自己实现的，它只是表示了一种可能。
这个一般开发中用不到，只有特别场景下，对于性能要求比较苛刻的情况下，才需要对底部的代码重写。

### String(char[] value, int off, int len, Void sig)

这个构造方法是String类内部的包级构造方法，
换句话说：这个构造方法只能在同类和同包下使用，其中有两处处构造方法最终调用的都是这个构造方法：

```java
    public String(char value[]) {
        this(value, 0, value.length, null);
    }
 
    public String(char value[], int offset, int count) {
        this(value, offset, count, rangeCheck(value, offset, count));
    }

     // JDK11 就没有这个构造方法了，JDK9 还存在
    String(char[] val, boolean share) {
            // assert share : "unshared not supported";
            this(val, 0, val.length, null);
    }

    String(char[] value, int off, int len, Void sig) {
        // len参数的含义就是需要存储的字符数组的长度 进行判断
        if (len == 0) {
            this.value = "".value;
            this.coder = "".coder;
            return;
        }
        // 判断的COMPACT_STRINGS压缩标志
        if (COMPACT_STRINGS) {
             // 为true，它就会采用LATIN1编码的方式对字符数组进行压缩，只存储字符中的低八位，丢弃高八位
             // compress方法其实就是将value中的每个字符循环遍历，并将其强制转换成byte类型放入byte数组中
            byte[] val = StringUTF16.compress(value, off, len);
            if (val != null) {
                this.value = val;
                this.coder = LATIN1;
                return;
            }
        }
        // 把高八位和低八位的数据全部存储，
        // 所以这里的toBytes方法会再起内部调用newBytesFor()方法创建一个长度为value数组长度2倍的byte数组，
        // 每个字符拆分成高八位和第八位存储在byte数组中相邻的两个位置
        this.coder = UTF16;
        this.value = StringUTF16.toBytes(value, off, len);
    }
    
```
主要看上面最后一个构造方法，它是包级私有的构造方法，首先可以看到它的最后一个参数是Void，
其实它更像是一个占位符，它是Java中关键字void的包装类，没有什么特殊的使用，仅仅是一个占位参数，
源码的注释上说它的作用就是为了消除对于其他公共构造函数可能造成的歧义，
也就是说会存在其他的公共构造方法与该方法的前三个参数一模一样，
第四个参数就是为了区分它们，并且该方法只能在同包级类中使用，
但是这个构造方法最主要的功能就是将传入的字符数组转换成byte数组。

这里先来仔细看一下它的代码逻辑：

- 首先对传入的len进行了判断，因为这里是构造函数，而len参数的含义就是需要存储的字符数组的长度，
这里将其与0进行比较，如果等于0，很明显就是一个空字符串了，所以采用了无参构造方法的那种处理方式，对value和coder进行赋值。

-如果len不是0，说明需要构造的字符串对象中是有内容的，在构造之前，判断的COMPACT_STRINGS压缩标志，
如果为true，它就会采用LATIN1编码的方式对字符数组进行压缩，只存储字符中的低八位，丢弃高八位。
这里的compress方法其实就是将value中的每个字符循环遍历，并将其强制转换成byte类型放入byte数组中。

-如果不需要进行压缩，就会挨个遍历字符数组value中的每个字符，将不丢失任何数据，
把高八位和低八位的数据全部存储，所以这里的toBytes方法会再起内部调用newBytesFor()方法创建一个长度为value数组长度2倍的byte数组，
每个字符拆分成高八位和第八位存储在byte数组中相邻的两个位置。

 
### String(int[] codePoints, int offset, int count)

这个构造函数是从JDK1.5以后加入的，比较特殊的是它的第一个参数是一个int数组，
它里面的内容其实是存储Unicode的code point值，
该方法的作用就是从codePints数组中截取一定长度的子数组构造字符串对象。
offset表示的是截取的开始位置，count表示需要截取的长度。
```java
    /**
     * @since  1.5
     */
    public String(int[] codePoints, int offset, int count) {
        checkBoundsOffCount(offset, count, codePoints.length);
        if (count == 0) {
            this.value = "".value;
            this.coder = "".coder;
            return;
        }
        if (COMPACT_STRINGS) {
            byte[] val = StringLatin1.toBytes(codePoints, offset, count);
            if (val != null) {
                this.coder = LATIN1;
                this.value = val;
                return;
            }
        }
        this.coder = UTF16;
        this.value = StringUTF16.toBytes(codePoints, offset, count);
    }
```

它的内部逻辑其实跟上面的逻辑差不多，只是转换的数据不一样，前面那个构造方法是将字符数组转换成byte数组，
这个是将int数组转换成byte数组。也是有一个压缩的过程，根据标志位，
如果COMPACT_STRINGS，就会将int数组中的每一个int元素强制转换成byte类型。
不过这里面的int值不是普通的值，它必须是Unicode的code point值。

复杂一点的是它的非压缩方法[StringUTF16.toBytes方法](05、StringUTF16源码分析.md)，
它里面需要判断int数组中的每个值是不是合法的CodePoint值。

### String(byte bytes[], int offset, int length, String charsetName)

       这个构造方法可以通过传入的byte数组和指定的字符集来构建一个byte子数组的字符串对象，
bytes就是需要截取并编码成字符的原数组，offset是byte数组的开始位置，length是需要截取的长度，charsetName是字符集名称。


```java
public String(byte bytes[], int offset, int length, String charsetName)
    throws UnsupportedEncodingException {
    //必须指定字符集名称，否则会抛NPE异常
    if (charsetName == null)
        throw new NullPointerException("charsetName");
    checkBoundsOffCount(offset, length, bytes.length);
    //这里实际上就是将传入的数据编码成指定的数据格式，Result是StringCoding的一个静态内部类
    //它是作为一种存放编码后的数据类型而存在，它内部会有byte数组和byte类型的coder
    StringCoding.Result ret =
        StringCoding.decode(charsetName, bytes, offset, length);
    this.value = ret.value;
    this.coder = ret.coder;
}
//...此处省略部分代码
//检查是否存在下标越界的情况
static void checkBoundsOffCount(int offset, int count, int length) {
    if (offset < 0 || count < 0 || offset > length - count) {
        throw new StringIndexOutOfBoundsException(
            "offset " + offset + ", count " + count + ", length " + length);
    }
}

```
还有一个类似与该构造方法的定义，只是最后一个参数由String变成了Charset类：


```java
public String(byte bytes[], int offset, int length, Charset charset) {
    if (charset == null)
        throw new NullPointerException("charset");
    checkBoundsOffCount(offset, length, bytes.length);
    StringCoding.Result ret =
        StringCoding.decode(charset, bytes, offset, length);
    this.value = ret.value;
    this.coder = ret.coder;
}

```
       可以看到它的逻辑基本与上面的逻辑一致，只是在字符集指定的时候，需要构造Charset对象。
注释上说明：该构造方法可以采用Charset中默认的替换字符替换那些输入格式错误字符以及那些不可映射的字符序列。

       不同于上一种String类型的charset参数，含有Charset类型参数的构造方法起源于JDK1.6，上面那个构造方法则是在JDK1.1中引入的。

### String(byte bytes[], int offset, int length)
       这个构造方法是根据传入的byte数组以及截取位置和长度，
构建一个平台默认的编码格式的字符串对象，可以看到它的参数传入没有字符集指定，请看源码：

```java
public String(byte bytes[], int offset, int length) {
    checkBoundsOffCount(offset, length, bytes.length);
    StringCoding.Result ret = StringCoding.decode(bytes, offset, length);
    this.value = ret.value;
    this.coder = ret.coder;
}

```
//下面是StringCoding类中的decode方法

```java
static Result decode(byte[] ba, int off, int len) {
    //defaultCharset()方法请看下面的源码截图，目的就是为了获取当前平台默认的字符集编码
    String csn = Charset.defaultCharset().name();
    try {
        //使用提供了缓存的变体decode方法
        return decode(csn, ba, off, len);
    } catch (UnsupportedEncodingException x) {
        warnUnsupportedCharset(csn);
    }
    try {
        //如果上面的decode出现异常，就采用默认的iso-8859-1编码
        return decode("ISO-8859-1", ba, off, len);
    } catch (UnsupportedEncodingException x) {
        //如果iso-8859-1编码在虚拟机初始化期间存在冲突，err(String)方法将会是获取错误信息的唯一途径
        err("ISO-8859-1 charset not available: " + x.toString() + "\n");
        //如果找不到iso-8859-1编码(它是必须的一个编码集，换句话说，系统必定存在这个编码集)
        //这种情况是比较严重了，属于系统环境初始化出现了问题，直接退出
        System.exit(1);
        return null;
    }
}

```
//获取系统默认的字符集编码
```java

public static Charset defaultCharset() {
    if (defaultCharset == null) {
        synchronized (Charset.class) {
            //这里的file.encoding参数是我们在代码编译时，所指定的编码参数
            //我们知道在javac命令在编译的时候可以指定encoding参数
            //我们很多的IDE都会在编译时传入一些默认的参数,编码格式就是其中一个，当然这个可以自行设定
            String csn = GetPropertyAction
                .privilegedGetProperty("file.encoding");
            //根据指定的编码集名称，查找是否存在这种编码格式
            Charset cs = lookup(csn);
            if (cs != null)
                defaultCharset = cs;
            else //如果查找不到就是默认的utf-8的编码格式
                defaultCharset = forName("UTF-8");
        }
    }
    return defaultCharset;
}

```
String(StringBuffer buffer) 和 String(StringBuilder builder)

这两个构造方法很简单，就是调用对应StringBuffer或者StringBuilder类中定义的toString方法，
而它们定义的toString方法的细节后面在说到对应类的时候再深入分析。




