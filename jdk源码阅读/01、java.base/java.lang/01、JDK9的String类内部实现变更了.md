
## [原文1](https://my.oschina.net/netconst/blog/1542362)

## [原文2](https://my.oschina.net/u/2518341/blog/2208675?nocache=1539908560270)


# JDK9的String类内部实现变更了

> 基于 JDK11

- [参考](../../../jdk9/Java9后String的空间优化.md)

- 从 JDK9开始，String的底层实现不一样了，具体内容如下

## 介绍

String 是一个 final class，是在下面的模块里 
```java

module java.base {
    
}

```

## 源码

- 先来看下JDK8的String的底层
```java
/** The value is used for character storage. */
    private final char value[];

```
使用的是char[]，即char数组

每个char占16个bit，Character.SIZE的值是16。

- JDK9中的String

```java
 @Stable
 private final byte[] value;
```
每个byte 占8个bit，Byte.SIZE 的值是 8，

JDK9中这么设计的原因，是因为大部分的String其实是Latin-1，
如果熟悉ASCII之类的编码就好理解了，
Latin-1只是占有了很少的bit位，用char来存储浪费了很多的空间。

JDK9中，字符如果是Latin-1，那么只用一个byte来存储，
否则用俩个byte来存储。

下面代码中的coder的值只有俩个，即LATIN1或者UTF16
```java
   byte coder() {
        return COMPACT_STRINGS ? coder : UTF16;
    }

    byte[] value() {
        return value;
    }

    private boolean isLatin1() {
        return COMPACT_STRINGS && coder == LATIN1;
    }

    @Native static final byte LATIN1 = 0;
    @Native static final byte UTF16  = 1;
```
## JDK9 之后 string 的实现

JDK9 之后String 的实现不再是char的数组了，改为byte数组 + coder。
我们都知道java中char是16位UTF16编码的，那么马上就会有个问题，byte数组是如何存下char数组的？

```java

    public String(char value[]) {
        this(value, 0, value.length, null);
    }
    
```

```java
    String(char[] value, int off, int len, Void sig) {
        if (len == 0) {
            this.value = "".value;
            this.coder = "".coder;
            return;
        }
         // 默认初始值是true
        if (COMPACT_STRINGS) {
            // 这里是通过 StringUTF16.compress(value, off, len); 来判断，
            // 如果char数组存在 value > 0xFF 的值时，就返回null，见下图：
            // Latin1是ISO-8859-1的别名 
            // ISO-8859-1编码是单字节编码，向下兼容ASCII，其编码范围是0x00-0xFF
            byte[] val = StringUTF16.compress(value, off, len);
            if (val != null) {
                this.value = val;
                this.coder = LATIN1;
                return;
            }
        }
        this.coder = UTF16;
        this.value = StringUTF16.toBytes(value, off, len);
    }
```
这里是通过 StringUTF16.compress(value, off, len); 来判断，如果char数组存在 value > 0xFF 的值时，就返回null，
```java
    public static byte[] compress(char[] val, int off, int len) {
        byte[] ret = new byte[len];
        if (compress(val, off, ret, 0, len) == len) {
            return ret;
        }
        return null;
    }
    
    public static byte[] compress(char[] val, int off, int len) {
        byte[] ret = new byte[len];
        if (compress(val, off, ret, 0, len) == len) {
            return ret;
        }
        return null;
    }
    
    @HotSpotIntrinsicCandidate
    public static int compress(char[] src, int srcOff, byte[] dst, int dstOff, int len) {
        for (int i = 0; i < len; i++) {
            char c = src[srcOff];
             // Latin1是ISO-8859-1的别名 
            // ISO-8859-1编码是单字节编码，向下兼容ASCII，其编码范围是0x00-0xFF
            // 判断 char c 是否大于 0xFF 【这个是16进制值，十进制为 255】
            if (c > 0xFF) {
                len = 0;
                break;
            }
            dst[dstOff] = (byte)c;
            srcOff++;
            dstOff++;
        }
        return len;
    }
    
        
```

如果char数组所有的字符都是小于0xFF，那么正好让一个byte对应一个char，String构造到此结束。

只要存在一个char > 0xFF，那么将会把byte数组的长度改为两倍char数组的长度，用两个字节存放一个char ，
```java
  @HotSpotIntrinsicCandidate
    public static byte[] toBytes(char[] value, int off, int len) {
        // new 一个两倍长度的 byte 数组
        byte[] val = newBytesFor(len);
        for (int i = 0; i < len; i++) {
            // 用两个字节来充当数组
            putChar(val, i, value[off]);
            off++;
        }
        return val;
    }
```

```java
    public static byte[] newBytesFor(int len) {
        if (len < 0) {
            throw new NegativeArraySizeException();
        }
        if (len > MAX_LENGTH) {
            throw new OutOfMemoryError("UTF16 String size is " + len +
                                       ", should be less than " + MAX_LENGTH);
        }
        return new byte[len << 1];
    }
```

```java
    @HotSpotIntrinsicCandidate
    // intrinsic performs no bounds checks
    static void putChar(byte[] val, int index, int c) {
        assert index >= 0 && index < length(val) : "Trusted caller missed bounds check";
        index <<= 1;
        val[index++] = (byte)(c >> HI_BYTE_SHIFT);
        val[index]   = (byte)(c >> LO_BYTE_SHIFT);
    }
```

由于实现机制的变动，所有的String方法都重新实现了一遍，但对外的接口还是保持一致的。
重构带来的最大好处就是在字符串中所有的字符都小于0xFF的情况下，会节省一半的内存。

老外估计早就想改了，老子可能一辈子做的东西都用不到0xFF之上的字符，却要占我一倍的内存！！哈哈

## JDK9 之后 string charAt 方法的实现

来看个有特点的方法，charAt(index)，在JDK9上是怎么实现的，
如下代码所示

 - charAt(index)的实现

```java
   public char charAt(int index) {
        // 判断 value 是否是拉丁
        if (isLatin1()) {
            return StringLatin1.charAt(value, index);
        } else {
            return StringUTF16.charAt(value, index);
        }
    }
```

- StringLatin1的charAt的实现

```java
    public static char charAt(byte[] value, int index) {
        if (index < 0 || index >= value.length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return (char)(value[index] & 0xff);
    }    
```

- StringUTF16中charAt的实现

```java
    public static char charAt(byte[] value, int index) {
        checkIndex(index, value);
        return getChar(value, index);
    }
    
    @HotSpotIntrinsicCandidate
    // intrinsic performs no bounds checks
    static char getChar(byte[] val, int index) {
        assert index >= 0 && index < length(val) : "Trusted caller missed bounds check";
        index <<= 1;
        return (char)(((val[index++] & 0xff) << HI_BYTE_SHIFT) |
                      ((val[index]   & 0xff) << LO_BYTE_SHIFT));
    }
```
StringUTF16的charAt实现较为复杂，因为它要从byte[]数组中取出俩个byte，组为char。



## [关于 & 0xff 参考1](../../../basic/0xff什么意思？.md)
## [关于 & 0xff 参考2](../../../basic/java一些加密算法中为什么要将每个字节都&0xff?.md)
## [关于 & 0xff 参考3](../../../basic/java字节&0xFF什么意思，<=0xF又是什么意思.md)



