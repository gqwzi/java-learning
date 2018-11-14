


# StringUTF16源码分析


## toBytes方法

下面的源代码中，后下面的后三个方法都是其他类中的，因为toBytes方法中有使用到，这里为了方便看代码，将其放在一起了。

在toBytes方法中，首先它需要计算char数组的精确长度，通过判断条件可知，
只有codePoint值在0X000000到0X10FFFF之间的值，n的值才会加1，
换句话说：只要isValidCodePoint判断通过，就说明当前字符需要两个字符位才能存储，
否则直接一个字符位就可存储，所以不会让n加1 。

经过遍历并计算之后，最后得到的n的值其实就是字符数组的长度，然后将其转换成byte数组的时候，
因为是StringUTF16格式，所以byte数组中需要连续两个位置来存储一个字符，
所以照这么计算：如果是合法的CodePint值，一个数值在底层byte数组中实际占用了四个位置。
其他类型的数值只会占据两个byte位置。


```java
public static byte[] toBytes(int[] val, int index, int len) {
    final int end = index + len;
    // Pass 1: 计算用于存储数据的字符数组的精确长度
    int n = len;
    //这里根据代码可以发现，对于isBmpCodePoint方法判断的数字，默认只是占用一个位置
    //但是对于isValidCodePoint方法判断的数字，需要多一个位置空间存储
    for (int i = index; i < end; i++) {
        int cp = val[i];
        if (Character.isBmpCodePoint(cp))
            continue;
        else if (Character.isValidCodePoint(cp))
            n++;
        else throw new IllegalArgumentException(Integer.toString(cp));
    }
    // Pass 2: 申请并用<high, low>对来填充byte数组
    byte[] buf = newBytesFor(n);
    for (int i = index, j = 0; i < end; i++, j++) {
        int cp = val[i];
        if (Character.isBmpCodePoint(cp)) {
            putChar(buf, j, cp);
        } else {
            putChar(buf, j++, Character.highSurrogate(cp));
            putChar(buf, j, Character.lowSurrogate(cp));
        }
    }
    return buf;
}
//......此处省略部分代码
//判断传入的codePoint是否在'\u0000'到'\uFFFF'之间，所以数组中只要一个位置就能存储
public static boolean isBmpCodePoint(int codePoint) {
    return codePoint >>> 16 == 0;
}
//......此处省略部分代码
//这个方法的作用就是判断传入的codePint值是否在0X000000到0X10FFFF之间，数据长度大，需要两个位置存储
public static boolean isValidCodePoint(int codePoint) {
    // Optimized form of:
    //     codePoint >= MIN_CODE_POINT && codePoint <= MAX_CODE_POINT
    int plane = codePoint >>> 16;
    //MAX_CODE_POINT = 0X10FFFF
    return plane < ((MAX_CODE_POINT + 1) >>> 16);
}    
//.....此处省略部分代码
static void putChar(byte[] val, int index, int c) {
    assert index >= 0 && index < length(val) : "Trusted caller missed bounds check";
    index <<= 1;
    val[index++] = (byte)(c >> HI_BYTE_SHIFT);
    val[index]   = (byte)(c >> LO_BYTE_SHIFT);
}

```