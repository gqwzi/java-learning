
## [原文1](https://blog.csdn.net/wungmc/article/details/17713521)

## [原文2](https://my.oschina.net/andyfeng/blog/1592690)

# java一些加密算法中为什么要将每个字节都 & 0xff?

java的md5算法中，需要将字节数组的hash value转换成十六进制，代码如下：

```java
org.apache.rocketmq.common.consistenthash
public class ConsistentHashRouter<T extends Node> {
  //default hash function
    private static class MD5Hash implements HashFunction {
        MessageDigest instance;

        public MD5Hash() {
            try {
                instance = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
            }
        }

        @Override
        public long hash(String key) {
            instance.reset();
            instance.update(key.getBytes());
            byte[] digest = instance.digest();

            long h = 0;
            for (int i = 0; i < 4; i++) {
                h <<= 8;
                // 强制转换成int 类型
                h |= ((int) digest[i]) & 0xFF;
            }
            return h;
        }
    }
}
```

代码中要将每个字节都 & 0xff，为什么呢？ 

这就涉及到计算机基础知识：

&是位与运算：
```
1 & 1 = 1

0 & 1 = 0

0 & 0 = 0
```

0xff是十六进制数（十进制为255），因为其是int类型，所以二进制表示

为：0000 0000 0000 0000 0000 0000 1111 1111

这样 a & 0xff 的意思就是取a的低八位。

例1：

``` 
byte b = 2;  
int i = b & 0xff;
```

分析：

1、b的二进制为：0000 0010 ，要和int类型的0xff运算，首先要进行类型转换，将b转换成int类型，

2、int类型的2的二进制为：0000 0000 0000 0000 0000 0000 0000 0010

3、与0xff进行&运算，结果i为：0000 0000 0000 0000 0000 0000 0000 0010

发现结果仍然是2，没有变，那位啥还要 &0xff呢？这是因为虽然正数不变，可负数就不一样了，负数如果不&0xff 就会错。

> 注意：负数在计算机中是以补码的形式存在的，补码的计算方法如下：

1、原数取绝对值；

2、将其二进制按位取反（1变0,0变1）

3、加1

例2：

-2的补码计算

``` 
1、2的绝对值：0000 0010

2、取反：1111 1101

3、加1:1111 1110
```

例3：
```
byte b = -2;  
int i = b & 0xff; 
```
分析：

1、b的二进制为：1111 1110

2、转换成int类型的-2，其二进制为：1111 1111 1111 1111 1111 1111 1111 1110

>（原来是0xfe，现在变成了0xfffffffe，发生了符号补位，这个结果就不对了，所以需要将高位清除这些补位）

3、与0xff进行&运算，结果i为：0000 0000 0000 0000 0000 0000 1111 1110 （取了低8位，十六进制为fe）


## 补位扩展：

窄的整型转换成较宽的整型时符号扩展规则：如果最初的数值类型是有符号的，
那么就执行符号扩展（即如果符号位 为1，则扩展为1，如果为零，则扩展为0）；

如果它是char，那么不管它将要被提升成什么类型，都执行零扩展。

byte是有符号的，所以进行符号位扩展，如例3中的-2，因为我们需要的是0扩展，所以进行&0xff消除高位。

char是没有符号的 