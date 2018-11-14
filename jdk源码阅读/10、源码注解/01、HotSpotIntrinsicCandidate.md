

# 注解 @HotSpotIntrinsicCandidate

这个注解是HotSpot虚拟机特有的注解，
使用了该注解的方法，它表示该方法在HotSpot虚拟机内部可能会自己来编写内部实现，
用以提高性能，但是它并不是必须要自己实现的，它只是表示了一种可能。

这个一般开发中用不到，只有特别场景下，对于性能要求比较苛刻的情况下，才需要对底部的代码重写。
 
 
 
该注解是特定于Java虚拟机的注解。通过该注解表示的方法可能( 但不保证 )通过HotSpot VM自己来写汇编或IR编译器来实现该方法以提供性能。

它表示注释的方法可能（但不能保证）由HotSpot虚拟机内在化。
如果HotSpot VM用手写汇编和/或手写编译器IR（编译器本身）替换注释的方法以提高性能，则方法是内在的。

也就是说虽然外面看到的在JDK9中weakCompareAndSet和compareAndSet底层依旧是调用了一样的代码，
但是不排除HotSpot VM会手动来实现weakCompareAndSet真正含义的功能的可能性。


[原文](https://cloud.tencent.com/developer/article/1152644)

[原文](https://www.jianshu.com/p/1d5065fd6675)