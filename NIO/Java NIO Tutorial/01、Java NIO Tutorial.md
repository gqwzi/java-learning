

## [原文](http://tutorials.jenkov.com/java-nio/channel-to-channel-transfers.html)

# Java NIO Tutorial

Java NIO (New IO) is an alternative(代替) IO API for Java (from Java 1.4), 
meaning alternative to the standard Java IO and Java Networking API's. 
Java NIO offers a different way of working with IO than the standard IO API's.

## Java NIO: Channels and Buffers
In the standard IO API you work with byte streams and character streams. 
In NIO you work with channels and buffers. Data is always read from a channel into a buffer, 
or written from a buffer to a channel.

## Java NIO: Non-blocking IO
Java NIO enables you to do non-blocking IO. 
For instance, a thread can ask a channel to read data into a buffer. 
While the channel reads data into the buffer, the thread can do something else. 
Once data is read into the buffer, the thread can then continue processing it. 
The same is true for writing data to channels.

## Java NIO: Selectors
Java NIO contains the concept of "selectors". 
A selector is an object that can monitor multiple channels for events (like: connection opened, data arrived etc.). 
Thus, a single thread can monitor multiple channels for data.

How all this works is explained in more detail in the next text in this series - the Java NIO overview.
> (翻译：所有这些工作原理将在本系列的下一篇文章 - Java NIO概述中详细解释)

> series ：系列

### Next: [Java NIO Overview](02、Java NIO Overview.md)