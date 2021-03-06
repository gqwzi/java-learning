

## [原文](https://blog.csdn.net/wangyangzhizhou/article/details/80446222)

# 06、StringBuilder 源码分析

## 概况
在 Java 中处理字符串时经常会使用 String 类，实际上 String 对象的值是一个常量，一旦创建后不能被改变。
正是因为其不可变，所以也无法进行修改操作，只有不断地 new 出新的 String 对象。

为此 Java 引入了可变字符串变量 StringBuilder 类，它不是线程安全的，只用在单线程场景下。

![](../../../images/source/string/stringbuilder_buffer.png)


## 类定义

```java
public final class StringBuilder
    extends AbstractStringBuilder
    implements java.io.Serializable, Comparable<StringBuilder>, CharSequence {
    
    }
```

StringBuilder 类被声明为 final，说明它不能再被继承。同时它继承了 AbstractStringBuilder 类，
并实现了 Serializable 和 CharSequence 两个接口。

其中 Serializable 接口表明其可以序列化。

CharSequence 接口用来实现获取字符序列的相关信息，接口定义如下：
 
* length()获取字符序列长度。 

* charAt(int index)获取某个索引对应字符。 

* subSequence(int start, int end)获取指定范围子字符串。 

* toString()转成字符串对象。 

* chars()用于获取字符序列的字符的 int 类型值的流，该接口提供了默认的实现。 

* codePoints()用于获取字符序列的代码点的 int 类型的值的流，提供了默认的实现。
```java
public interface CharSequence {

    int length();

    char charAt(int index);

    CharSequence subSequence(int start, int end);

    public String toString();

    public default IntStream chars() {
        省略代码。。
    }

    public default IntStream codePoints() {
        省略代码。。
    }
}
```
 
## 主要属性
```java
byte[] value;

byte coder;

int count;
```
 
- value 该数组用于存储字符串值。

- coder 表示该字符串对象所用的编码器。

- count 表示该字符串对象中已使用的字符数。

## 构造方法

有若干种构造方法，可以指定容量大小参数，如果没有指定则构造方法默认创建容量为16的字符串对象。
如果 COMPACT_STRINGS 为 true，即使用紧凑布局则使用 LATIN1 编码（ISO-8859-1编码），
则开辟长度为16的 byte 数组。而如果是 UTF16 编码则开辟长度为32的 byte 数组。

```java

public StringBuilder() {
        super(16);
    }

AbstractStringBuilder(int capacity) {
        if (COMPACT_STRINGS) {
            value = new byte[capacity];
            coder = LATIN1;
        } else {
            value = StringUTF16.newBytesFor(capacity);
            coder = UTF16;
        }
    }

public StringBuilder(int capacity) {
        super(capacity);
    }
    
```

如果构造函数传入的参数为 String 类型，则会开辟长度为str.length() + 16的 byte 数组，
并通过append方法将字符串对象添加到 byte 数组中。

```java
public StringBuilder(String str) {
        super(str.length() + 16);
        append(str);
    }

```

类似地，传入参数为 CharSequence 类型时也做相同处理。


```java
public StringBuilder(CharSequence seq) {
        this(seq.length() + 16);
        append(seq);
    }

```

## 主要方法

### append方法

有多个append方法，都只是传入的参数不同而已，下面挑几个典型的深入看看，其他都是类似的处理。

如果传入 String 类型参数则调用父类的append方法将字符串对象添加到 StringBuilder 的 byte 数组中，然后返回 this。
append 的逻辑为：
 
* String 对象为 null的话则在 StringBuilder 的 byte 数组中添加n u l l四个字符。 

* 通过ensureCapacityInternal方法确保有足够的空间，如果没有则需要重新开辟空间。 

* 通过putStringAt方法将字符串对象里面的 byte 数组复制到 StringBuilder 的 byte 数组中，使用了System.arraycopy进行复制。 

* count 为已使用的字符数，将其加上复制的字符串长度。 

* 返回 this。

```java
public StringBuilder append(String str) {
        super.append(str);
        return this;
    }

public AbstractStringBuilder append(String str) {
        if (str == null) {
            return appendNull();
        }
        int len = str.length();
        ensureCapacityInternal(count + len);
        putStringAt(count, str);
        count += len;
        return this;
    }
    
```

ensureCapacityInternal方法逻辑： 

* 首先获取现有的容量大小。 

* 如果需要的容量大于现有容量，则需要扩充容量，并且将原来的数组复制过来。 

* newCapacity方法用于确定新容量大小，将现有容量大小扩大一倍再加上2，如果还是不够大则直接等于需要的容量大小，
另外，如果新容量大小为负则容量设置为MAX_ARRAY_SIZE，它的大小等于Integer.MAX_VALUE - 8。


```java
private void ensureCapacityInternal(int minimumCapacity) {
        int oldCapacity = value.length >> coder;
        if (minimumCapacity - oldCapacity > 0) {
            value = Arrays.copyOf(value,
                    newCapacity(minimumCapacity) << coder);
        }
    }

private int newCapacity(int minCapacity) {
        int oldCapacity = value.length >> coder;
        int newCapacity = (oldCapacity << 1) + 2;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        int SAFE_BOUND = MAX_ARRAY_SIZE >> coder;
        return (newCapacity <= 0 || SAFE_BOUND - newCapacity < 0)
            ? hugeCapacity(minCapacity)
            : newCapacity;
    }
    
```
putStringAt的逻辑： 

* String 对象的编码和 StringBuilder 对象的编码不相同，则先执行inflate方法转换成 UTF16 编码。 

* 如果 StringBuilder 对象不是 Latin1 编码则不执行转换。 

* 通过StringUTF16.newBytesFor扩充空间，因为UTF16编码的占位是 Latin1 编码的两倍。 

* 通过StringLatin1.inflate将原来的值拷贝到扩充后的空间中。 

* 通过str.getBytes将 String 对象的值拷贝到 StringBuilder 对象中。


```java
private final void putStringAt(int index, String str) {
        if (getCoder() != str.coder()) {
            inflate();
        }
        str.getBytes(value, index, coder);
    }

private void inflate() {
        if (!isLatin1()) {
            return;
        }
        byte[] buf = StringUTF16.newBytesFor(value.length);
        StringLatin1.inflate(value, 0, buf, 0, count);
        this.value = buf;
        this.coder = UTF16;
    }
    
```

传入的参数为 CharSequence 类型时，他会分几种情况处理，如果为空则添加null字符。
另外还会根据对象实例化自 String 类型或 AbstractStringBuilder 类型调用对应的append方法。

```java
public StringBuilder append(CharSequence s) {
        super.append(s);
        return this;
    }

public AbstractStringBuilder append(CharSequence s) {
        if (s == null) {
            return appendNull();
        }
        if (s instanceof String) {
            return this.append((String)s);
        }
        if (s instanceof AbstractStringBuilder) {
            return this.append((AbstractStringBuilder)s);
        }
        return this.append(s, 0, s.length());
    }
    
```

传入的参数为 char 数组类型时，逻辑如下： 

* 通过ensureCapacityInternal方法确保足够容量。 

* append 过程中根据不同编码做不同处理。 

* 如果是 Latin1 编码，从偏移量开始将一个个字符赋值到 StringBuilder 对象的字节数组中，
这个过程中会检测每个字符是否可以使用 Latin1 编码来解码，可以的话则直接将 char 转成 byte 并进行赋值操作。
否则为 UTF16 编码，此时先通过inflate()扩展空间，
然后再通过StringUTF16.putCharsSB将所有剩下的字符串以 UTF16 编码保存到 StringBuilder 对象中。 

* 如果是 UTF16 编码，则直接通过StringUTF16.putCharsSB将 char 数组添加到 StringBuilder 对象中。 

* 修改 count 属性，即已使用的字节数。


```java
public StringBuilder append(char[] str) {
        super.append(str);
        return this;
    }

public AbstractStringBuilder append(char[] str) {
        int len = str.length;
        ensureCapacityInternal(count + len);
        appendChars(str, 0, len);
        return this;
    }

private final void appendChars(char[] s, int off, int end) {
        int count = this.count;
        if (isLatin1()) {
            byte[] val = this.value;
            for (int i = off, j = count; i < end; i++) {
                char c = s[i];
                if (StringLatin1.canEncode(c)) {
                    val[j++] = (byte)c;
                } else {
                    this.count = count = j;
                    inflate();
                    StringUTF16.putCharsSB(this.value, j, s, i, end);
                    this.count = count + end - i;
                    return;
                }
            }
        } else {
            StringUTF16.putCharsSB(this.value, count, s, off, end);
        }
        this.count = count + end - off;
    }

```

传入的参数为 boolean 类型时，逻辑如下： 

* 通过ensureCapacityInternal确定容量足够大，true 和 false 的长度分别为4和5。 

* 如果为 Latin1 编码，按条件将t r u e 和 f a l s e添加到 StringBuilder 对象的字节数组中。 

* 如果为 UTF16 编码，则按照编码格式将 t r u e 和 f a l s e添加到 StringBuilder 对象的字节数组中。


```java
public StringBuilder append(boolean b) {
        super.append(b);
        return this;
    }

public AbstractStringBuilder append(boolean b) {
        ensureCapacityInternal(count + (b ? 4 : 5));
        int count = this.count;
        byte[] val = this.value;
        if (isLatin1()) {
            if (b) {
                val[count++] = 't';
                val[count++] = 'r';
                val[count++] = 'u';
                val[count++] = 'e';
            } else {
                val[count++] = 'f';
                val[count++] = 'a';
                val[count++] = 'l';
                val[count++] = 's';
                val[count++] = 'e';
            }
        } else {
            if (b) {
                count = StringUTF16.putCharsAt(val, count, 't', 'r', 'u', 'e');
            } else {
                count = StringUTF16.putCharsAt(val, count, 'f', 'a', 'l', 's', 'e');
            }
        }
        this.count = count;
        return this;
    }
    
```

如果传入的参数为 int 或 long 类型，则处理的大致逻辑都为先计算整数一共多少位数，
然后再一个个放到 StringBuilder 对象的字节数组中。比如“789”，
长度为3，对于 Latin1 编码则占3个字节，而 UTF16 编码占6个字节。


```java
public StringBuilder append(int i) {
        super.append(i);
        return this;
    }

public StringBuilder append(long lng) {
        super.append(lng);
        return this;
    }
    
```


如果传入的参数为 float 或 double 类型，则处理的大致逻辑都为先计算浮点数一共多少位数，
然后再一个个放到 StringBuilder 对象的字节数组中。比如“789.01”，长度为6，注意点也占空间，
对于 Latin1 编码则占6个字节，而 UTF16 编码占12个字节。


```java
public StringBuilder append(float f) {
        super.append(f);
        return this;
    }

public StringBuilder append(double d) {
        super.append(d);
        return this;
    }
 
```
### appendCodePoint方法
该方法用于往 StringBuilder 对象中添加代码点。代码点是 unicode 编码给字符分配的唯一整数，
unicode 有17个代码平面，其中的基本多语言平面（Basic Multilingual Plane，BMP）包含了主要常见的字符，其余平面叫做补充平面。

所以这里先通过Character.isBmpCodePoint判断是否属于 BMP 平面，如果属于该平面，此时只需要2个字节，
则直接转成 char 类型并添加到 StringBuilder 对象。如果超出 BMP 平面，
此时需要4个字节，分别用来保存 High-surrogate 和 Low-surrogate，
通过Character.toChars完成获取对应4个字节并添加到 StringBuilder 对象中。

```java
public StringBuilder appendCodePoint(int codePoint) {
        super.appendCodePoint(codePoint);
        return this;
    }

public AbstractStringBuilder appendCodePoint(int codePoint) {
        if (Character.isBmpCodePoint(codePoint)) {
            return append((char)codePoint);
        }
        return append(Character.toChars(codePoint));
    }

```

### delete方法
该方法用于将指定范围的字符删掉，逻辑为： 

* end 不能大于已使用字符数 count，大于的话则令其等于 count。 

* 通过checkRangeSIOOBE检查范围合法性。 

* 通过shift方法实现删除操作，其通过System.arraycopy来实现，即把 end 后面的字符串复制到 start 位置，即相当于将中间的字符删掉。 

* 修改已使用字符数 count 值。 

* 返回 this。

```java
public StringBuilder delete(int start, int end) {
        super.delete(start, end);
        return this;
    }

public AbstractStringBuilder delete(int start, int end) {
        int count = this.count;
        if (end > count) {
            end = count;
        }
        checkRangeSIOOBE(start, end, count);
        int len = end - start;
        if (len > 0) {
            shift(end, -len);
            this.count = count - len;
        }
        return this;
    }

private void shift(int offset, int n) {
        System.arraycopy(value, offset << coder,
                         value, (offset + n) << coder, (count - offset) << coder);
    }
```

### deleteCharAt方法
删除指定索引字符，与 delete 方法实现一样，通过shift方法实现删除，修改 count 值。

```java
public StringBuilder deleteCharAt(int index) {
        super.deleteCharAt(index);
        return this;
    }

public AbstractStringBuilder deleteCharAt(int index) {
        checkIndex(index, count);
        shift(index + 1, -1);
        count--;
        return this;
    }
```

### replace方法

该方法用于将指定范围的字符替换成指定字符串。逻辑如下： 

* end 不能大于已使用字符数 count，大于的话则令其等于 count。 

* 通过checkRangeSIOOBE检查范围合法性。 

* 计算新 count。 

* 通过shift方法把 end 后面的字符串复制到 end + (newCount - count) 位置。 

* 更新 count。 

* 通过putStringAt将字符串放到 start 后，直接覆盖掉后面的若干字符即可。

```java
public StringBuilder replace(int start, int end, String str) {
        super.replace(start, end, str);
        return this;
    }

public AbstractStringBuilder replace(int start, int end, String str) {
        int count = this.count;
        if (end > count) {
            end = count;
        }
        checkRangeSIOOBE(start, end, count);
        int len = str.length();
        int newCount = count + len - (end - start);
        ensureCapacityInternal(newCount);
        shift(end, newCount - count);
        this.count = newCount;
        putStringAt(start, str);
        return this;
    }
```

### insert方法
该方法用于向 StringBuilder 对象中插入字符。
根据传入的参数类型有若干个 insert 方法，操作都相似，深入看重点一个。

当传入的参数为 String 类型时，逻辑为： 

* 通过checkOffset检查偏移量的合法性。
 
* 如果字符串为空，则将null字符串赋值给它。 

* 通过ensureCapacityInternal确保足够的容量。 

* 通过shift方法把 offset 后面的字符串复制到 offset+len 位置。 

* 更新 count。 

* 将 str 放到 offset 位置，完成插入操作。 

* 返回 this。
```
public StringBuilder insert(int offset, String str) {
        super.insert(offset, str);
        return this;
    }

public AbstractStringBuilder insert(int offset, String str) {
        checkOffset(offset, count);
        if (str == null) {
            str = "null";
        }
        int len = str.length();
        ensureCapacityInternal(count + len);
        shift(offset, len);
        count += len;
        putStringAt(offset, str);
        return this;
    }
 ```
 
除此之外，还可能插入 boolean 类型、object 类型、char 类型、char 数组类型、
float 类型、double 类型、long 类型、int 类型和 CharSequence 类型。几乎都是先转成 String 类型再插入。

### indexOf方法
该方法用于查找指定字符串的索引值，可以从头开始查找，也可以指定起始位置。

可以看到它间接调用了 String 类的indexOf方法，核心逻辑是如果是 Latin1 编码则通过StringLatin1.indexOf查找，
而如果是 UTF16 编码则通过StringUTF16.indexOf查找。
如果要查找的字符串编码和 StringBuilder 对象的编码不相同，则通过StringUTF16.indexOfLatin1查找。

```java

public int indexOf(String str) {
        return super.indexOf(str);
    }

public int indexOf(String str, int fromIndex) {
        return super.indexOf(str, fromIndex);
    }

public int indexOf(String str, int fromIndex) {
        return String.indexOf(value, coder, count, str, fromIndex);
    }

static int indexOf(byte[] src, byte srcCoder, int srcCount,
                       String tgtStr, int fromIndex) {
        byte[] tgt    = tgtStr.value;
        byte tgtCoder = tgtStr.coder();
        int tgtCount  = tgtStr.length();

        if (fromIndex >= srcCount) {
            return (tgtCount == 0 ? srcCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (tgtCount == 0) {
            return fromIndex;
        }
        if (tgtCount > srcCount) {
            return -1;
        }
        if (srcCoder == tgtCoder) {
            return srcCoder == LATIN1
                ? StringLatin1.indexOf(src, srcCount, tgt, tgtCount, fromIndex)
                : StringUTF16.indexOf(src, srcCount, tgt, tgtCount, fromIndex);
        }
        if (srcCoder == LATIN1) {   
            return -1;
        }
        return StringUTF16.indexOfLatin1(src, srcCount, tgt, tgtCount, fromIndex);
    }
```

Latin1 编码的StringLatin1.indexOf的主要逻辑为：先确定要查找的字符串的第一个字节 first，
然后在 value 数组中遍历寻找等于 first 的字节，一旦找到等于第一个字节的元素，
则比较剩下的字符串是否相等，如果所有都相等则查找到指定的字节数组，返回该索引值，否则返回-1。

```java
public static int indexOf(byte[] value, int valueCount, byte[] str, int strCount, int fromIndex) {
        byte first = str[0];
        int max = (valueCount - strCount);
        for (int i = fromIndex; i <= max; i++) {
            if (value[i] != first) {
                while (++i <= max && value[i] != first);
            }
            if (i <= max) {
                int j = i + 1;
                int end = j + strCount - 1;
                for (int k = 1; j < end && value[j] == str[k]; j++, k++);
                if (j == end) {
                    return i;
                }
            }
        }
        return -1;
    }
```

UTF16 编码的StringUTF16.indexOf逻辑与 Latin1 编码类似，只不过是需要两个字节合到一起（即比较 char 类型）进行比较。

另外如果源字符串的编码为 UTF16，而查找的字符串编码为 Latin1 编码， 
则通过StringUTF16.indexOfLatin1来查找，查找逻辑也是类似，
只不过需要把一个字节的 Latin1 编码转成两个字节的 UTF16 编码后再比较。

### lastIndexOf方法
该方法用于从尾部开始反向查找指定字符串的索引值，可以从最末尾开始查找，也可以指定末尾位置。
它的实现逻辑跟indexOf差不多，只是反过来查找，这里不再赘述。

```java
public int lastIndexOf(String str) {
        return super.lastIndexOf(str);
    }

public int lastIndexOf(String str, int fromIndex) {
        return super.lastIndexOf(str, fromIndex);
    }
```

### reverse方法
该方法用于将字符串反转，实现逻辑如下，其实就是做一个反转操作，遍历整个 StringBuilder 对象的数组，实现反转。
其中分为 LATIN1 编码和 UTF16 编码做不同处理。

```java
public StringBuilder reverse() {
        super.reverse();
        return this;
    }

public AbstractStringBuilder reverse() {
        byte[] val = this.value;
        int count = this.count;
        int coder = this.coder;
        int n = count - 1;
        if (COMPACT_STRINGS && coder == LATIN1) {
            for (int j = (n-1) >> 1; j >= 0; j--) {
                int k = n - j;
                byte cj = val[j];
                val[j] = val[k];
                val[k] = cj;
            }
        } else {
            StringUTF16.reverse(val, count);
        }
        return this;
    }
```

### toString方法
该方法用于返回 String 对象，根据不同的编码分别 new 出 String 对象。
其中 UTF16 编码会尝试压缩成 LATIN1 编码，失败的话则以 UTF16 编码生成 String 对象。
```java
public String toString() {
        return isLatin1() ? StringLatin1.newString(value, 0, count)
                          : StringUTF16.newString(value, 0, count);
    }

public static String newString(byte[] val, int index, int len) {
        return new String(Arrays.copyOfRange(val, index, index + len),
                          LATIN1);
    }

public static String newString(byte[] val, int index, int len) {
        if (String.COMPACT_STRINGS) {
            byte[] buf = compress(val, index, len);
            if (buf != null) {
                return new String(buf, LATIN1);
            }
        }
        int last = index + len;
        return new String(Arrays.copyOfRange(val, index << 1, last << 1), UTF16);
    }
```
### writeObject方法
该方法是序列化方法，先按默认机制将对象写入，然后再将 count 和 char 数组写入。
```java
private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        s.defaultWriteObject();
        s.writeInt(count);
        char[] val = new char[capacity()];
        if (isLatin1()) {
            StringLatin1.getChars(value, 0, count, val, 0);
        } else {
            StringUTF16.getChars(value, 0, count, val, 0);
        }
        s.writeObject(val);
    }
```

### readObject方法

该方法是反序列方法，先按默认机制读取对象，再读取 count 和 char 数组，最后再初始化对象内的字节数组和编码标识。
```java
private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        count = s.readInt();
        char[] val = (char[]) s.readObject();
        initBytes(val, 0, val.length);
    }

void initBytes(char[] value, int off, int len) {
        if (String.COMPACT_STRINGS) {
            this.value = StringUTF16.compress(value, off, len);
            if (this.value != null) {
                this.coder = LATIN1;
                return;
            }
        }
        this.coder = UTF16;
        this.value = StringUTF16.toBytes(value, off, len);
    }
``` 