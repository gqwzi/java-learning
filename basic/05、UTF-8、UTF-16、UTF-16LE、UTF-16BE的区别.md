
## [原文](http://mocha-c-163-com.iteye.com/blog/583064)

# UTF-8, UTF-16, UTF-16LE, UTF-16BE的区别

## 最近遇到的麻烦事 

Charset 里的问题, 一般我们都用unicode来作为统一编码, 但unicode也有多种表现形式 

首先, 我们说的unicode, 其实就是utf-16, 但最通用的却是utf-8, 

原因: 我猜大概是英文占的比例比较大, 这样utf-8的存储优势比较明显, 
因为utf-16是固定16位的(双字节), 而utf-8则是看情况而定, 
即可变长度, 常规的128个ASCII只需要8位(单字节), 而汉字需要24位 

## UTF-16, UTF-16LE, UTF-16BE, 及其区别BOM 

> BOM(字节顺序标记, byte-order mark)

同样都是unicode, 为什么要搞3种这么麻烦? 

先说UTF-16BE (big endian), 比较好理解的, 俗称大头 
比如说char 'a', ascii为 
0x61, 那么它的utf-8, 则为 \[0x61], 但utf-16是16位的, 所以为\[0x00, 0x61] 

再说UTF-16LE(little endian), 俗称小头, 这个是比较常用的 
还是char 'a', 它的代码却反过来: \[0x61, 0x00], 据说是为了提高速度而迎合CPU的胃口, 
CPU就是这到倒着吃数据的, 这里面有汇编的知识, 不多说 

然后说UTF-16, 要从代码里自动判断一个文件到底是UTF-16LE还是BE, 对于单纯的英文字符来说还比较好办, 
但要有特殊字符, 图形符号, 汉字, 法文, 俄语, 火星语之类的话, 相信各位都很头痛吧, 

所以, unicode组织引入了BOM的概念, 即byte order mark, 顾名思义, 就是表名这个文件到底是LE还是BE的, 
其方法就是, 在UTF-16文件的头2个字节里做个标记: LE \[0xFF, 0xFE], BE \[0xFE, 0xFF] 
>【注意这里: 在java 查看UTF-16 占用字节的时候多出两个字节的原因 "我".getBytes("UTF-16").length 一般汉字占用两个字节】

理解了这个后, 在java里遇到utf-16还是会遇到麻烦, 因为要在文件里面单独判断头2个再字节是很不流畅的. 

## 小结: 
Java代码   
```java
InputStreamReader reader=new InputStreamReader(fin, charset)  

```

1. 如果这个UTF-16文件里带有BOM的话, charset就用"UTF-16", 
java会自动根据BOM判断LE还是BE, 
如果你在这里指定了"UTF-16LE"或"UTF-16BE"的话, 猜错了会生成乱七八糟的文件, 
哪怕猜对了, java也会把头2个字节当成文本输出给你而不会略过去, 因为\[FF FE]或\[FE FF]这2个代码没有内容, 
所以, windows会用"?"代替给你 

2. 如果这个UTF-16文件里不带BOM的话, 则charset就要用"UTF-16LE"或"UTF-16BE"来指定LE还是BE的编码方式


另外, UTF-8也有BOM的, \[0xEF, 0xBB, 0xBF], 但可有可无, 
但用windows的notepad另存为时会自动帮你加上这个, 而很多非windows平台的UTF8文件又没有这个BOM, 
真是难为我们这些程序员啊 

## 错误的例子 

1. 文件A, UTF16格式, 带BOM LE, 
```java
InputStreamReader reader=new InputStreamReader(fin, "utf-16le") 
```
会多输出一个"?"在第一个字节, 原因: java没有把头2位当成BOM 

2. 文件A, UTF16格式, 带BOM LE, 
```java
InputStreamReader reader=new InputStreamReader(fin, "utf-16be") 
```
会出乱码, 原因: 字节的高低位弄反了, 'a' 在文件里 \[0x61, 0x00], 但java以为'a'应该是 \[0x00 0x61] 

3. 文件A, UTF16格式, 带BOM BE, 
```java
InputStreamReader reader=new InputStreamReader(fin, "utf-16le") 
```
会出乱码, 原因: 字节的高低位弄反了, 'a' 在文件里 \[0x00, 0x61], 但java以为'a'应该是 \[0x61 0x00] 

4. 文件A, UTF16格式, 带BOM BE, 

```java
InputStreamReader reader=new InputStreamReader(fin, "utf-16be")
``` 
会多输出一个"?"在第一个字节, 原因: java没有把头2位当成BOM 

5. 文件A, UTF16格式, LE 不带BOM, 

```java
InputStreamReader reader=new InputStreamReader(fin, "utf-16")
``` 

会出乱码, 因为utf-16对于java来说, 默认为be(1.6JDK, 以后的说不准) 
但windows的notepad打开正常, 因为notepad默认为le, - -# 

6. 文件A, UTF16格式, BE 不带BOM, 

```java
InputStreamReader reader=new InputStreamReader(fin, "utf-16")
``` 

恭喜你, 蒙对了 
但winodws的notepad打开时, 每个字符中间都多了一个" ", 因为notepad把它当成ASNI了 

在windows下输出unicode文件 
通过java出来unicode文件, 也容易混淆 

```java
FileOutputStream fout=new FileOutputStream(file);  
OutputStreamWriter writer=new OutputStreamWriter(fout, charset);
```

- 1. charset为"UTF-16"时, java会默认添加BOM \[0xFE, 0xFF], 并以BE的格式编写byte 

- 2. charset为"UTF-16BE"时, java不会添加BOM, 但编码方式为 BE 

- 3. charset为"UTF-16LE"时, java不会添加BOM, 但编码方式为 LE 

以上通过 test.getByte("utf-16"), test.getByte("utf-16be"), test.getByte("utf-16le") 可以验证 

而windows的notepad默认的unicode为 LE, 并带BOM, 
所以, 推荐输出 UTF-16LE, 并人为添加BOM, 即: 
 
```java
byte[] bom={-1, -2};    //FF FE, java的byte用的是补码, 验证: b=127, b+=1, 而b=-128  
fout.write(bom);

```






## 推荐阅读



[分享一下我所了解的字符编码知识](https://www.jianshu.com/p/2d4ad873b39f)