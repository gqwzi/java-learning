
## [原文](https://www.jianshu.com/p/daf4c2f59141)

## [原文](https://juejin.im/post/5b6825bb6fb9a04f9963ca49)

# 02、String源码阅读(三：方法)


## 主要方法

### length方法

字符串的长度应该是字符的长度，而不是字节数组的长度，所以这里做了右移操作，
LATIN1 编码时coder()为0，字符串长度等于字节数组长度。
UTF16 编码时coder()为1，字符串等于字节数组长度一半。

String类中获取字符串的长度通过length()方法，
其实调用的是数组的length属性，所以String没有length属性。
 
```java
public int length() {
        // 字符串的长度也与编码相关，计算时通过右移来实现。
        // 如果是 LATIN-1 编码，则右移0位，数组长度即为字符串长度。
        // 而如果是 UTF16 编码，则右移1位，数组长度的二分之一为字符串长度。
        return value.length >> coder();
    }
    
    byte coder() {
        //这里根据压缩标识，来返回byte值，其中UTF16的值是1，coder是不确定的
        //但是可以确定的是，coder的值跟对应编码方式下一个字符所占的byte数组长度有关
        //例如：UTF16类型的数据，一个字符会占据两个byte数组位，所以计算长度时需要将byte数组长度缩小2倍
        return COMPACT_STRINGS ? coder : UTF16;
    }
```
它返回的是字符串的长度，length的数值其实是字符串中Unicode的code units，即：编码单元数目。

### isEmpty方法
通过判断 byte 数组长度是否为0来判断字符串对象是否为空。
```java

public boolean isEmpty() {
        return value.length == 0;
    }
    
```
### charAt方法

返回字符串中指定下标index所在的那个字符，index的取值方位必须是0到length-1的方位，
并且字符串的第一个字符的下标是0，也就是说是从0开始计数。
  
方法用于返回指定索引处的字符。索引范围为从0 到length() - 1

取字符需要根据编码来操作，如果是 LATIN1 编码，则直接取 byte 数组中对应索引的元素，并转成 char 类型即可。

如果是 UTF16 编码，因为它每个 UTF16 编码占用两个字节，所以需要将索引乘以2后作为最终索引取得两个字节并转换成 char 类型，
具体实现逻辑如getChar方法所示。
```java
/**
* 参数
  index -- 字符的索引。
  
  返回值
  返回指定索引处的字符。
*/
public char charAt(int index) {
    //判断是否是压缩格式版的字符串
    if (isLatin1()) {
        //压缩版：放弃高八位
        return StringLatin1.charAt(value, index);
    } else {
        //同时保留高八位和低八位
        return StringUTF16.charAt(value, index);
    }
}
private boolean isLatin1() {
    return COMPACT_STRINGS && coder == LATIN1;
}
//StringLatin1.charAt
public static char charAt(byte[] value, int index) {
    if (index < 0 || index >= value.length) {
        throw new StringIndexOutOfBoundsException(index);
    }
    //这里使用了0xff，它就是十进制的255，二进制就是11111111
    //对于Java中，byte类型转成int，高位会随机填充值，通过它来保证低位可靠性，并且放弃高位数
    //因为&操作中，超过0xff的部分，全部都会变成0，而对于0xff以内的数据，它不会影响原来的值
    // 如果是负数那么需要 &操作 https://github.com/pankui/java-learning/blob/master/basic/java%E5%AD%97%E8%8A%82%260xFF%E4%BB%80%E4%B9%88%E6%84%8F%E6%80%9D%EF%BC%8C%3C%3D0xF%E5%8F%88%E6%98%AF%E4%BB%80%E4%B9%88%E6%84%8F%E6%80%9D.md
    return (char)(value[index] & 0xff);
}
//StringUTF16.charAt
public static char charAt(byte[] value, int index) {
    checkIndex(index, value);
    return getChar(value, index);
}
static char getChar(byte[] val, int index) {
    assert index >= 0 && index < length(val) : "Trusted caller missed bounds check";
    index <<= 1;
    //HI_BYTE_SHIFT和LO_BYTE_SHIFT跟当前系统环境有关，存在一个大端还是小端的判定isBigEndian()
    //所谓大小端：大端就是高字节存储在低位，低字节存储在高位，小端正好与之想反
    return (char)(((val[index++] & 0xff) << HI_BYTE_SHIFT) |
                  ((val[index]   & 0xff) << LO_BYTE_SHIFT));
}
static {
    if (isBigEndian()) {
        HI_BYTE_SHIFT = 8;
        LO_BYTE_SHIFT = 0;
    } else {
        HI_BYTE_SHIFT = 0;
        LO_BYTE_SHIFT = 8;
    }
}
```
仔细考虑上面的StringUTF16.charAt方法，它有一个index<<=1的操作，之所以会这样，
也是跟byte存储有关，因为StringUTF16不会丢弃任何一位，所以它的byte数组相邻两个位置一个存储高八位，一个存储低八位。
所以任何传入的index，如果换算成byte数组中的位置需要将其值扩展成二倍作为index的开始，
并且byte数组存储这些数据的时候，将高八位放在数组前一个位置，低八位放在数组的后一个位置，
形成<hight, low>类型的数据。再根据大端小端确认到底是高八位需要移动还是低八位需要移动。

### equals方法

String重写了equals方法，当且仅当传入的参数是一个String类型，并且其中的字符序列与调用对象中的字符序列相同才会返回true。
  
用于比较两字符串对象是否相等，如果引用相同则返回 true。
否则判断比较对象是否为 String 类的实例，是的话转成 String 类型，
接着比较编码是否相同，分别以 LATIN1 编码和 UTF16 编码进行比较。


```java
public boolean equals(Object anObject) {
    //首先用==比较，如果相等，说明就是同一个对象，肯定是相等的
    if (this == anObject) {
        return true;
    }
    //前置判断：必须是String类型，否则肯定不相等
    if (anObject instanceof String) {
        String aString = (String)anObject;
        //coder方法其实就是获取字符串采用的编码方式，如果编码方式都不一样，肯定结果为false
        if (coder() == aString.coder()) {
            //根据数据是否是压缩数据，采用不同的比较方式
            //数据压缩弃了高八位，一个八位就只占据一个byte数组位
            //如果是非压缩版，一个字符对应两个byte数组位
            return isLatin1() ? StringLatin1.equals(value, aString.value)
                : StringUTF16.equals(value, aString.value);
        }
    }
    return false;
}
byte coder() {
    return COMPACT_STRINGS ? coder : UTF16;
}
private boolean isLatin1() {
    return COMPACT_STRINGS && coder == LATIN1;
}
//StringLatin1.equals
public static boolean equals(byte[] value, byte[] other) {
    //首先需要保证byte数组长度必定相同
    if (value.length == other.length) {
        //逐个比对两个数组内部相同下标上的内容
        for (int i = 0; i < value.length; i++) {
            if (value[i] != other[i]) {
                return false;
            }
        }
        return true;
    }
    return false;
}
//StringUTF16.equals
public static boolean equals(byte[] value, byte[] other) {
    if (value.length == other.length) {
        int len = value.length >> 1;
        for (int i = 0; i < len; i++) {
            //这里使用的getChar方法就是前面charAt方法中的那个getChar方法
            //其实就是根据index和byte获取高位和低位拼接后得到的字符，然后比较char是否不同
            if (getChar(value, i) != getChar(other, i)) {
                return false;
            }
        }
        return true;
    }
    return false;
}
```


### hashCode方法

我电脑上当前的JDK版本中，hash码的计算分了两种情况：StringLatin1计算和StringUTF16计算，即：分为压缩情况下的hash计算和非压缩的hash计算。

在JDK的注释中，提出了一个hash的计算公式：

```java

s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
//其实上面的也可以看成是：
s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]*31^0

```
  这里s就代表字符串内部的字符数组，n代表字符串的长度，这个是整体的计算思路，
但是JDK中字符串底层使用的是byte数组，所以字符还牵扯到具体的存储格式问题，存在格式压缩的情况。
这里使用31数字，网上有选用这个数计算的各种原因，可以查阅相关博客了解具体内容，
这里只要记住一点：31这个数是奇素数，它可以保证hashCode尽可能大的基础上，
虚拟机在计算的时候还会有一定的优化，要知道：31 * n == (n << 5) - n这个等式的结果是true，它是成立的。
 

该方法返回字符串对象的哈希值，如果已经有缓存了则直接返回，否则根据不同编码分别计算哈希值。

```java
public int hashCode() {
    int h = hash;
    if (h == 0 && value.length > 0) {
        hash = h = isLatin1() ? StringLatin1.hashCode(value)
                              : StringUTF16.hashCode(value);
    }
    return h;
}

    
```

下面分别是 Latin1 编码和 UTF16 编码的哈希值计算逻辑，
遍历地执行h = 31 * h + (v & 0xff) 和 h = 31 * h + getChar(value, i)运算。

```java
//StringLatin1.hashCode
//对于压缩版的数据，它的字符长度其实与value的长度是一致的，直接套用上面的公式就可以了
public static int hashCode(byte[] value) {
    int h = 0;
    //for循环结束之后，就可以得到一个赋值公式：
    //value[0]*31^(n-1) + ... + value[n - 1]
    for (byte v : value) {
        h = 31 * h + (v & 0xff);
    }
    return h;
}
//StringUTF16.hashCode
//大致的算法没变，只是需要处理以下value数组中内容，数组中连续两个位置合在一起才表示一个字符
//getChar方法的作用就是根据i值获取i以及i+1位置上的两个byte数据，并且合并成一个char返回
public static int hashCode(byte[] value) {
    int h = 0;
    int length = value.length >> 1;
    for (int i = 0; i < length; i++) {
        h = 31 * h + getChar(value, i);
    }
    return h;
}
    
```

### compareTo(String anotherString)

```java
public int compareTo(String anotherString) {
    byte v1[] = value;
    byte v2[] = anotherString.value;
    if (coder() == anotherString.coder()) {
        return isLatin1() ? StringLatin1.compareTo(v1, v2)
                          : StringUTF16.compareTo(v1, v2);
    }
    return isLatin1() ? StringLatin1.compareToUTF16(v1, v2)
                      : StringUTF16.compareToLatin1(v1, v2);
 }

```
compareTo是String实现的Comparable接口中的方法，逻辑比较简单，就是将两个字符串对应的byte数组进行比较，
逐个byte位置进行比较，不同的是StringUTF16是两两byte位置进行比较，所以会有一个转换操作，
将连续两个byte数组中的内容组合起来转成char类型再进行比较，但是最终的逻辑都是一样，逐个比较String里面的字符。


### indexOf方法

indexOf方法有很多重载的方法，该方法的作用是可以获取一个字符在字符串中的位置，
不同的重载方法只是针对这个功能做了不同的业务划分，下面逐个讨论。

该方法用于查找字符串中第一个出现某字符或字符串的位置，有多种方法参数。
可传入 int 类型，也可传入 String 类型，另外还能传入开始位置。
根据编码的不同分别调用 StringLatin1 和 StringUTF16 的indexOf方法。

```java
//这个是经常使用的一种，传入一个字符串，获取字符串在源字符串中第一次出现的位置
//注意，这里有个情况，就是字符串可以是多个字符，而不单单只是一个字符的字符串
public int indexOf(String str) {
    if (coder() == str.coder()) {
        return isLatin1() ? StringLatin1.indexOf(value, str.value)
            : StringUTF16.indexOf(value, str.value);
    }
    if (coder() == LATIN1) {  // str.coder == UTF16
        return -1;
    }
    return StringUTF16.indexOfLatin1(value, str.value);
}
//StringLatin1.indexOf
public static int indexOf(byte[] value, byte[] str) {
    //如果传入的是一个空字符串，默认直接返回0
    if (str.length == 0) {
        return 0;
    }
    //如果源字符串本身是一个空字符串，返回-1
    if (value.length == 0) {
        return -1;
    }
    return indexOf(value, value.length, str, str.length, 0);
}
public static int indexOf(byte[] value, int valueCount, byte[] str, int strCount, int fromIndex) {
    byte first = str[0];
    //这里的max计算可以这么理解：
    //源byte长度减去待比较byte数组长度，得到的值是可以完全包含待比较数组的最大index
    //换句话说，如果超出了这个值，那么剩余的byte位数与待比较的byte数组长度就不一致了，肯定不具有可比性了
    int max = (valueCount - strCount);
    for (int i = fromIndex; i <= max; i++) {
        //查询第一个源byte数组的开始位字符内容是否与待比较数据的首位是否相等
        if (value[i] != first) {
            //如果不相等就继续寻找源数组的下一位，直至找到与待比较数组的首位内容相等的位置
            while (++i <= max && value[i] != first);
        }
        // 到此处，说明已经发现了一位相等的数据，或者i的值已经超出max
        if (i <= max) {
            //排除i超出max的可能，剩下的就是在已经找到的首字符的基础上，继续查询余下的字符
            //所以这里的j是从i所示的位置的下一位开始，结束的index为开始位+待比较数组长度-1
            int j = i + 1;
            int end = j + strCount - 1;
            //一个没有循环体的循环，为的就是比较待比较数组中剩余的内容与源数组i之后的内容，
            //连续strCount位之内的数据是否相等，如果完全相等，i就是需要的值
            //中间若存在不同的情况，说明待比较数组不是源数组的一个子数组，继续循环直至结束返回-1
            for (int k = 1; j < end && value[j] == str[k]; j++, k++);
            if (j == end) {
                // Found whole string.
                return i;
            }
        }
    }
    return -1;
}

    
```

Latin1 编码查找逻辑，

- 判断 int 值是否能转成 byte，方法是看右移8位是否为0，为0即说明除了低8位其他都为0。

- 判断索引值的合法性并修正。

- int 值转成 byte 类型。

- 遍历检查数组中哪个值相等并返回对应索引值。

- 查找不到就返回-1。

```java
public static int indexOf(byte[] value, int ch, int fromIndex) {
        if (!canEncode(ch)) {
            return -1;
        }
        int max = value.length;
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            return -1;
        }
        byte c = (byte)ch;
        for (int i = fromIndex; i < max; i++) {
            if (value[i] == c) {
               return i;
            }
        }
        return -1;
    }

```


```java
public static boolean canEncode(int cp) {
        return cp >>> 8 == 0;
    }

```

类似地，对于 UTF16 编码也做类似处理，
但因为 unicode 包含了基本多语言平面（Basic Multilingual Plane，BMP）外，还存在补充平面。
而传入的值为 int 类型（4字节），所以如果超出 BMP 平面，此时需要4个字节，
分别用来保存 High-surrogate 和 Low-surrogate，此时就需要对比4个字节。
另外，如果查找子字符串则是从子字符串第一个字符开始匹配直到子字符串完全被匹配成功。


### lastIndexOf方法
该方法用于返回指定字符在此字符串中最后一次出现处的索引，有多种方法参数。
可传入 int 类型，也可传入 String 类型，另外还能传入开始位置。
根据编码的不同分别用 Latin1 和 UTF16 两种方式处理。
```java

public int lastIndexOf(int ch) {
        return lastIndexOf(ch, length() - 1);
    }
    
public int lastIndexOf(int ch, int fromIndex) {
        return isLatin1() ? StringLatin1.lastIndexOf(value, ch, fromIndex)
                          : StringUTF16.lastIndexOf(value, ch, fromIndex);
    }
    
public int lastIndexOf(String str) {
        return lastIndexOf(str, length());
    }
    
public int lastIndexOf(String str, int fromIndex) {
        return lastIndexOf(value, coder(), length(), str, fromIndex);
    }
    
static int lastIndexOf(byte[] src, byte srcCoder, int srcCount,
                           String tgtStr, int fromIndex) {
        byte[] tgt = tgtStr.value;
        byte tgtCoder = tgtStr.coder();
        int tgtCount = tgtStr.length();
        int rightIndex = srcCount - tgtCount;
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        if (fromIndex < 0) {
            return -1;
        }
        if (tgtCount == 0) {
            return fromIndex;
        }
        if (srcCoder == tgtCoder) {
            return srcCoder == LATIN1
                ? StringLatin1.lastIndexOf(src, srcCount, tgt, tgtCount, fromIndex)
                : StringUTF16.lastIndexOf(src, srcCount, tgt, tgtCount, fromIndex);
        }
        if (srcCoder == LATIN1) {   
            return -1;
        }
        return StringUTF16.lastIndexOfLatin1(src, srcCount, tgt, tgtCount, fromIndex);
    }
    
```
Latin1 编码的逻辑为，

- 判断 int 值是否能转成 byte，方法是看右移8位是否为0，为0即说明除了低8位其他都为0。

- 通过Math.min(fromIndex, value.length - 1)取偏移值。

- 从偏移处开始往前遍历查找，找到即返回索引值。

- 找不到返回-1。


```java
public static int lastIndexOf(final byte[] value, int ch, int fromIndex) {
        if (!canEncode(ch)) {
            return -1;
        }
        int off  = Math.min(fromIndex, value.length - 1);
        for (; off >= 0; off--) {
            if (value[off] == (byte)ch) {
                return off;
            }
        }
        return -1;
    }
    
```
类似地，对于 UTF16 编码也做类似处理，但因为 unicode 包含了基本多语言平面（Basic Multilingual Plane，BMP）外，
还存在补充平面。而传入的值为 int 类型（4字节），所以如果超出 BMP 平面，此时需要4个字节，
分别用来保存 High-surrogate 和 Low-surrogate，此时就需要对比4个字节。

另外，如果查找子字符串则是从子字符串第一个字符开始匹配直到子字符串完全被匹配成功。


###  toString方法

直接返回 this。

```java
public String toString() {
        return this;
    }
    
```


```java
public static String format(String format, Object... args) {
        return new Formatter().format(format, args).toString();
    }

public static String format(Locale l, String format, Object... args) {
        return new Formatter(l).format(format, args).toString();
    }
    
```

###  valueOf方法

用于将传入的对象转成 String 对象，可传入多种类型参数。

- Objet 时，为空则返回"null"字符串，否则obj.toString()。

- char 数组时，直接new 一个 String 对象。

- boolean 时，返回"true" 或 "false"字符串。

- char 时，优先尝试转成 Latin1 编码的 String 读，否则用 UTF16。

- int 时，Integer.toString(i)。

- long 时，Long.toString(l)。

- float 时，Float.toString(f)。

- double 时，Double.toString(d)。

```java
public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }
    
public static String valueOf(char data[]) {
        return new String(data);
    }
    
public static String valueOf(char data[], int offset, int count) {
        return new String(data, offset, count);
    }
    
public static String valueOf(boolean b) {
        return b ? "true" : "false";
    }
    
public static String valueOf(char c) {
        if (COMPACT_STRINGS && StringLatin1.canEncode(c)) {
            return new String(StringLatin1.toBytes(c), LATIN1);
        }
        return new String(StringUTF16.toBytes(c), UTF16);
    }
    
public static String valueOf(int i) {
        return Integer.toString(i);
    }
    
public static String valueOf(long l) {
        return Long.toString(l);
    }
    
public static String valueOf(float f) {
        return Float.toString(f);
    }
    
public static String valueOf(double d) {
        return Double.toString(d);
    }
    
```


###  coder方法
获取字符串的编码，如果使用非紧凑布局则一定为 UTF16，否则可能为 Latin1 或 UTF16。

```java
byte coder() {
        return COMPACT_STRINGS ? coder : UTF16;
    }
    
```
###  isLatin1方法
判断是否为 Latin1 编码。必须是紧凑布局且为 LATIN1 才属于 Latin1 编码。

```java
private boolean isLatin1() {
        return COMPACT_STRINGS && coder == LATIN1;
    }
 
    
```

###  intern方法
一个 native 方法，具体实现可看前面的文章《深入谈谈String.intern()在JVM的实现》
```java
public native String intern();
```
       它是一个native方法，它会返回一个规范化表示的字符串对象。字符串常量池是由String自行维护的，
它初始时是空的。如果intern方法被调用，此时若常量池中已经存在一个字符串对象与调用方法的对象equals相等，
那么常量池中的字符串对象将被返回，否则该对象将被添加到常量池中并返回在常量池中的引用。

       任意两个字符串s和t，如果s.intern() == t.intern()的结果为true，那么s.equals(t)的结果必然也是true。
所有的字符串字面量和字符串常量表达式都会在常量池中。

 
因为篇幅有限，其他方法就不再一一介绍了，在前面介绍的基础上，基本看看源码都能大致了解的差不多。
 
