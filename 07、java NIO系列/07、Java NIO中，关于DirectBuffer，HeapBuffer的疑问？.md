

## [原文](https://www.zhihu.com/question/57374068/answer/152691891)

# Java NIO中，关于DirectBuffer，HeapBuffer的疑问？
``` 
1. DirectBuffer 属于堆外存，那应该还是属于用户内存，而不是内核内存？

2. FileChannel 的read(ByteBuffer dst)函数,write(ByteBuffer src)函数中，
如果传入的参数是HeapBuffer类型,则会临时申请一块DirectBuffer,进行数据拷贝，而不是直接进行数据传输，这是出于什么原因？
```

 
Java NIO中的direct buffer（主要是DirectByteBuffer）其实是分两部分的：

```      
 Java              |      native
                   |
 DirectByteBuffer  |     malloc'd
 [    address   ] -+-> [   data    ]
                   |
```

其中 DirectByteBuffer 自身是一个Java对象，在Java堆中；
而这个对象中有个long类型字段address，记录着一块调用 malloc() 申请到的native memory。
所以回到题主的问题：

## 1. DirectBuffer 属于堆外存，那应该还是属于用户内存，而不是内核内存？

DirectByteBuffer 自身是（Java）堆内的，它背后真正承载数据的buffer是在（Java）堆外——native memory中的。
这是 malloc() 分配出来的内存，是用户态的。

## 2. FileChannel 的read(ByteBuffer dst)函数,write(ByteBuffer src)函数中， 如果传入的参数是HeapBuffer类型,则会临时申请一块DirectBuffer,进行数据拷贝，而不是直接进行数据传输，这是出于什么原因？

题主看的是OpenJDK的 sun.nio.ch.IOUtil.write(FileDescriptor fd, ByteBuffer src, 
long position, NativeDispatcher nd) 的实现对不对： 
```java
  
 static int write(FileDescriptor fd, ByteBuffer src, long position,
                     NativeDispatcher nd)
        throws IOException
    {
        if (src instanceof DirectBuffer)
            return writeFromNativeBuffer(fd, src, position, nd);

        // Substitute a native buffer
        int pos = src.position();
        int lim = src.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        ByteBuffer bb = Util.getTemporaryDirectBuffer(rem);
        try {
            bb.put(src);
            bb.flip();
            // Do not update src until we see how many bytes were written
            src.position(pos);

            int n = writeFromNativeBuffer(fd, bb, position, nd);
            if (n > 0) {
                // now update src
                src.position(pos + n);
            }
            return n;
        } finally {
            Util.offerFirstTemporaryDirectBuffer(bb);
        }
    }
    
```
这里其实是在迁就OpenJDK里的HotSpot VM的一点实现细节。
HotSpot VM里的GC除了CMS之外都是要移动对象的，是所谓“compacting GC”。

如果要把一个Java里的 byte[] 对象的引用传给native代码，让native代码直接访问数组的内容的话，
就必须要保证native代码在访问的时候这个 byte[] 对象不能被移动，也就是要被“pin”（钉）住。

可惜HotSpot VM出于一些取舍而决定不实现单个对象层面的object pinning，要pin的话就得暂时禁用GC——也就等于把整个Java堆都给pin住。
HotSpot VM对JNI的Critical系API就是这样实现的。这用起来就不那么顺手。

所以 Oracle/Sun JDK / OpenJDK 的这个地方就用了点绕弯的做法。
它假设把 HeapByteBuffer 背后的 byte[] 里的内容拷贝一次是一个时间开销可以接受的操作，同时假设真正的I/O可能是一个很慢的操作。

于是它就先把 HeapByteBuffer 背后的 byte[] 的内容拷贝到一个 DirectByteBuffer 背后的native memory去，
这个拷贝会涉及 sun.misc.Unsafe.copyMemory() 的调用，背后是类似 memcpy() 的实现。
这个操作本质上是会在整个拷贝过程中暂时不允许发生GC的，虽然实现方式跟JNI的Critical系API不太一样。
（具体来说是 Unsafe.copyMemory() 是HotSpot VM的一个intrinsic方法，中间没有safepoint所以GC无法发生）。

然后数据被拷贝到native memory之后就好办了，就去做真正的I/O，把 DirectByteBuffer 背后的native memory地址传给真正做I/O的函数。
这边就不需要再去访问Java对象去读写要做I/O的数据了。