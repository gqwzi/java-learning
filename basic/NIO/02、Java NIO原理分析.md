
## [原文](https://www.cnblogs.com/jabnih/p/7076465.html)

# Java NIO原理分析

这里主要围绕着Java NIO展开，从Java NIO的基本使用，到介绍Linux下NIO API，再到Java Selector其底层的实现原理。

- Java NIO基本使用

- Linux下的NIO系统调用介绍

- Selector原理

- Channel和Buffer之间的堆外内存

## Java NIO基本使用
从JDK NIO文档里面可以发现，Java将其划分成了三大块：Channel，Buffer以及多路复用Selector。

- Channel的存在，封装了对什么实体的连接通道（如网络/文件）；

- Buffer封装了对数据的缓冲存储，

- 最后对于Selector则是提供了一种可以以单线程非阻塞的方式，来处理多个连接。

### 基本应用示例

NIO的基本步骤是:
- 创建Selector和ServerSocketChannel，

- 然后注册channel的ACCEPT事件

- 调用select方法，等待连接的到来，以及接收连接后将其注册到Selector中。

下面的为Echo Server的示例：
```java

public class SelectorDemo {

    public static void main(String[] args) throws IOException {


        Selector selector = Selector.open();
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(8080));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int ready = selector.select();
            if (ready == 0) {
                continue;
            } else if (ready < 0) {
                break;
            }

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {

                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel accept = channel.accept();
                    if (accept == null) {
                        continue;
                    }
                    accept.configureBlocking(false);
                    accept.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    // 读事件
                    deal((SocketChannel) key.channel(), key);
                } else if (key.isWritable()) {
                    // 写事件
                    resp((SocketChannel) key.channel(), key);
                }
                // 注：处理完成后要从中移除掉
                iterator.remove();
            }
        }
        selector.close();
        socketChannel.close();
    }

    private static void deal(SocketChannel channel, SelectionKey key) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer responseBuffer = ByteBuffer.allocate(1024);

        int read = channel.read(buffer);

        if (read > 0) {
            buffer.flip();
            responseBuffer.put(buffer);
        } else if (read == -1) {
            System.out.println("socket close");
            channel.close();
            return;
        }

        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        key.attach(responseBuffer);
    }

    private static void resp(SocketChannel channel, SelectionKey key) throws IOException {

        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.flip();

        channel.write(buffer);
        if (!buffer.hasRemaining()) {
            key.attach(null);
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}

```
## Linux下的NIO系统调用介绍

在Linux环境下，提供了几种方式可以实现NIO，如epoll，poll，select等。
对于select/poll，每次调用，都是从外部传入FD和监听事件，
这就导致每次调用的时候，都需要将这些数据从用户态复制到内核态，就导致了每次调用代价比较大，
而且每次从select/poll返回回来，都是全量的数据，需要自行去遍历检查哪些是READY的。

对于epoll，则为增量式的，系统内部维护了所需要的FD和监听事件，要注册的时候，调用epoll_ctl即可，
而每次调用，不再需要传入了，返回的时候，只返回READY的监听事件和FD。

下面作个简单的伪代码：
具体的可以看以前的文章：<http://www.cnblogs.com/jabnih/category/724636.html>

```c

// 1. 创建server socket
// 2. 绑定地址
// 3. 监听端口
// 4. 创建epoll
int epollFd = epoll_create(1024);
// 5. 注册监听事件
struct epoll_event event;
event.events = EPOLLIN | EPOLLRDHUP | EPOLLET;
event.data.fd = serverFd;
epoll_ctl(epollFd, EPOLL_CTL_ADD, serverFd, &event);

while(true) {
    readyNums = epoll_wait( epollFd, events, 1024, -1 );
    
    if ( readyNums < 0 )
     {
         printf("epoll_wait error\n");
         exit(-1);
     }

     for ( i = 0; i <  readyNums; ++i)
     {
         if ( events[i].data.fd == serverFd )
         {
             clientFd = accept( serverFd, NULL, NULL );
             // 注册监听事件
             ...
         }else if ( events[i].events & EPOLLIN )
         {
            // 处理读事件
         }else if ( events[i].events & EPOLLRDHUP )
         {
            // 关闭连接事件
            close( events[i].data.fd );
         }
}

```

## Selector原理

### SelectionKey

从Java顶层使用者角度来看，channel通过注册，返回SelectionKey，而Selector.select方法，也是通过返回SelectionKey来使用。
那么这里为什么会需要这个类呢？这个类有什么作用？无论是任何语言，其实都脱离不了系统底层的支持，
通过上述Linux下的基本应用，可以知道，通过系统调用，向其传递和返回的都是FD以及事件这些参数，
那么站在设计角度来看，就需要有一个映射关系，使得可以关联起来，
这里有Channel封装的是通过，如果将READY事件这些参数放在里面，不太合适，
这个时候，SelectionKey出现了，在SelectionKey内部，保存Channel的引用以及一些事件信息，
然后Selector通过FD找到SelectionKey来进行关联。在底层EP里面，就有一个属性：
> Map<Integer,SelectionKeyImpl> fdToKey。

### EPollSelectorImpl
在Linux 2.6+版本，Java NIO采用的epoll（即EPollSelectorImpl类），
对于2.4.x的，则使用poll（即PollSelectorImpl类）,这里以epoll为例。

### select方法

顶层Selector，通过调用select方法，最终会调用到EPollSelectorImpl.doSelect方法，通过该方法，
可以看到，其首先会处理一些不再注册的事件，调用pollWrapper.poll(timeout);，
然后再进行一次清理，最后，可以看到需要处理映射关系
```c
protected int doSelect(long timeout)
    throws IOException
{
    if (closed)
        throw new ClosedSelectorException();
    // 处理一些不再注册的事件
    processDeregisterQueue();
    try {
        begin();
        pollWrapper.poll(timeout);
    } finally {
        end();
    }
    // 再进行一次清理
    processDeregisterQueue();
    int numKeysUpdated = updateSelectedKeys();
    if (pollWrapper.interrupted()) {
        // Clear the wakeup pipe
        pollWrapper.putEventOps(pollWrapper.interruptedIndex(), 0);
        synchronized (interruptLock) {
            pollWrapper.clearInterrupted();
            IOUtil.drain(fd0);
            interruptTriggered = false;
        }
    }
    return numKeysUpdated;
}


private int updateSelectedKeys() {
    int entries = pollWrapper.updated;
    int numKeysUpdated = 0;
    for (int i=0; i<entries; i++) {
        // 获取FD
        int nextFD = pollWrapper.getDescriptor(i);
        // 根据FD找到对应的SelectionKey
        SelectionKeyImpl ski = fdToKey.get(Integer.valueOf(nextFD));
        // ski is null in the case of an interrupt
        if (ski != null) {
            // 找到该FD的READY事件
            int rOps = pollWrapper.getEventOps(i);
            if (selectedKeys.contains(ski)) {
                // 将底层的事件转换为Java封装的事件,SelectionKey.OP_READ等
                if (ski.channel.translateAndSetReadyOps(rOps, ski)) {
                    numKeysUpdated++;
                }
            } else {
                // 没有在原有的SelectedKey里面，说明是在等待过程中加入的
                ski.channel.translateAndSetReadyOps(rOps, ski);
                if ((ski.nioReadyOps() & ski.nioInterestOps()) != 0) {
                    // 需要更新selectedKeys集合
                    selectedKeys.add(ski);
                    numKeysUpdated++;
                }
            }
        }
    }
    // 返回Ready的Channel个数
    return numKeysUpdated;
}

```

### EPollArrayWrapper
EpollArrayWrapper封装了底层的调用，里面包含几个native方法，如：
```java

private native int epollCreate();
private native void epollCtl(int epfd, int opcode, int fd, int events);
private native int epollWait(long pollAddress, int numfds, long timeout,
                             int epfd) throws IOException;

```
在openjdk的native目录（native/sun/nio/ch）里面可以找到对应的实现EPollArrayWrapper.c。
（这里顺带提一下，要实现native方法，可以在类里的方法加上native关键字，然后编译成class文件，
再转换输出.h，c/c++底层实现该头文件的方法，编译成so库，放到对应目录即可）
在初始化文件方法里面，可以看到，是通过动态解析加载进来的，最终调用的epoll_create等方法。

```c
JNIEXPORT void JNICALL
Java_sun_nio_ch_EPollArrayWrapper_init(JNIEnv *env, jclass this)
{
    epoll_create_func = (epoll_create_t) dlsym(RTLD_DEFAULT, "epoll_create");
    epoll_ctl_func    = (epoll_ctl_t)    dlsym(RTLD_DEFAULT, "epoll_ctl");
    epoll_wait_func   = (epoll_wait_t)   dlsym(RTLD_DEFAULT, "epoll_wait");

    if ((epoll_create_func == NULL) || (epoll_ctl_func == NULL) ||
        (epoll_wait_func == NULL)) {
        JNU_ThrowInternalError(env, "unable to get address of epoll functions, pre-2.6 kernel?");
    }
}

```

### Channel和Buffer之间的堆外内存
经常会听见别人说，堆外内存容易泄漏，以及Netty框架里面采用了堆外内存，减少拷贝提高性能。
那么这里面的堆外内存指的是什么?之前怀着一个好奇心，通过read方法，最后追踪到SocketChannelImpl里面read方法，
里面调用了IOUtil的read方法。
里面会首先判断传入的Buffer是不是DirectBuffer，如果不是（则是HeapByteBuffer），
则会创建一个临时的DirectBuffer，然后再将其复制到堆内。IOUtil.read方法：

```java

static int read(FileDescriptor var0, ByteBuffer var1, long var2, NativeDispatcher var4, Object var5) throws IOException {
    if(var1.isReadOnly()) {
        throw new IllegalArgumentException("Read-only buffer");
    } else if(var1 instanceof DirectBuffer) {
        // 为堆外内存，则直接读取
        return readIntoNativeBuffer(var0, var1, var2, var4, var5);
    } else {
        // 为堆内内存，先获取临时堆外内存
        ByteBuffer var6 = Util.getTemporaryDirectBuffer(var1.remaining());

        int var8;
        try {
            // 读取到堆外内存
            int var7 = readIntoNativeBuffer(var0, var6, var2, var4, var5);
            var6.flip();
            if(var7 > 0) {
                // 复制到堆内
                var1.put(var6);
            }

            var8 = var7;
        } finally {
            // 释放临时堆外内存
            Util.offerFirstTemporaryDirectBuffer(var6);
        }

        return var8;
    }
}

```
这里有一个问题就是，为什么会需要DirectBuffer以及堆外内存？

通过对DirectByteBuffer的创建来分析，可以知道，通过unsafe.allocateMemory(size);
来分配内存的，而对于该方法来说，可以说是直接调用malloc返回，
这一块内存是不受GC管理的，也就是所说的：堆外内存容易泄漏。
但是对于使用DirectByteBuffer来说，会创建一个Deallocator，注册到Cleaner里面，当对象被回收的时候，
则会被直接，从而释放掉内存，减少内存泄漏。
要用堆外内存，从上面的创建来看，堆外内存创建后，以long型地址保存的，而堆内内存会受到GC影响，对象会被移动，
如果采用堆内内存，进行系统调用的时候，那么GC就需要停止，否则就会有问题，基于这一点，
采用了堆外内存（这一块参考了R大的理解：<https://www.zhihu.com/question/57374068>）。

注：堆外内存的创建（unsafe.cpp）：

// 仅仅作了对齐以及将长度放在数组前方就返回了
```c
UNSAFE_ENTRY(jlong, Unsafe_AllocateMemory(JNIEnv *env, jobject unsafe, jlong size))
  UnsafeWrapper("Unsafe_AllocateMemory");
  size_t sz = (size_t)size;
  if (sz != (julong)size || size < 0) {
    THROW_0(vmSymbols::java_lang_IllegalArgumentException());
  }
  if (sz == 0) {
    return 0;
  }
  sz = round_to(sz, HeapWordSize);
  void* x = os::malloc(sz);
  if (x == NULL) {
    THROW_0(vmSymbols::java_lang_OutOfMemoryError());
  }
  //Copy::fill_to_words((HeapWord*)x, sz / HeapWordSize);
  return addr_to_java(x);
UNSAFE_END

``

