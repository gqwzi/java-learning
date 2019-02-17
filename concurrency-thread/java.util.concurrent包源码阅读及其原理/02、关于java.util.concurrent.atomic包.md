
# [原文](https://www.cnblogs.com/wanly3643/p/2829119.html)

# 02 关于java.util.concurrent.atomic包

Atomic数据类型有四种类型：AtomicBoolean, AtomicInteger, AtomicLong, 
和AtomicReference(针对Object的)以及它们的数组类型，
还有一个特殊的AtomicStampedReference,它不是AtomicReference的子类，
而是利用AtomicReference实现的一个储存引用和Integer组的扩展类
 
首先，所有原子操作都是依赖于sun.misc.Unsafe这个类，这个类底层是由C++实现的，
利用指针来实现数据操作
 
## [关于CAS](../01、多线程并发知识点.md)
一种无锁机制，比较并交换, 操作包含三个操作数 
- 内存位置（V）
- 预期原值（A）
- 新值(B)。

```java
    /**
     * Atomically increments by one the current value.
     *
     * @return the previous value
     */
    public final int getAndIncrement() {
        return unsafe.getAndAddInt(this, valueOffset, 1);
    }
```

如果内存位置的值与预期原值相匹配，那么处理器会自动将该位置值更新为新值。
否则，处理器不做任何操作。
无论哪种情况，它都会在 CAS 指令之前返回该位置的值。
CAS 有效地说明了“我认为位置 V 应该包含值 A；如果包含该值，
则将 B 放到这个位置；否则，不要更改该位置，只告诉我这个位置现在的值即可”
 
好处：操作系统级别的支持，效率更高，无锁机制，降低线程的等待，
实际上是把这个任务丢给了操作系统来做。
 
这个理论是整个java.util.concurrent包的基础。
 
## 关于sun.misc.Unsafe
 
几个疑问

- 1）四个基本类型的compareAndSet和weakCompareAndSet实现是一样的？

- 2）AtomicLong的set方法非线程安全的，为啥？
并非线程不全，而是对于Long的Updater，会有VM_SUPPORTS_LONG_CAS，
如果JVM的long操作是原子化的，会采用无锁的CAS来更新，
如果不支持就会使用带锁的方式来更新。
 
AtomicXXXX四个数值类型

- 1.value成员都是volatile
```java
public class AtomicInteger extends Number implements java.io.Serializable {
    // 其他省略
    private volatile int value;
    }
```

- 2.基本方法get/set

- 3.compareAndSet \
    weakCompareAndSet, \
    lazySet: 使用Unsafe按照顺序更新参考Unsafe的C++实现）\
    getAndSet：取当前值，使用当前值和准备更新的值做CAS
    
- 4.对于Long和Integer

getAndIncrement/incrementAndGet \
getAndDecrement/decrementAndGet \
getAndAdd/addAndGet

三组方法都和getAndSet，取当前值，加减之得到准备更新的值，
再做CAS，/左右的区别在于返回的是当前值还是更新值。
 
关于数组

- 1.没有Boolean的Array，可以用Integer代替，底层实现完全一致，
毕竟AtomicBoolean底层就是用Integer实现

- 2.数组变量volatile没有意义，因此set/get就需要Unsafe来做了,方法构成与上面一致，
但是多了一个index来指定操作数组中的哪一个元素。
 
关于FieldUpdater

- 1 利用反射原理，实现对一个类的某个字段的原子化更新，
该字段类型必须和Updater要求的一致，例如如果使用 AtomicIntegerFieldUpdater，
字段必须是Integer类型，而且必须有volatile限定符。
Updater的可以调用的方 法和数字类型完全一致，额外增加一个该类型的对象为参数，
updater就会更新该对象的那个字段了。

- 2 Updater本身为抽象类，但有一个私有化的实现，利用门面模式，
在抽象类中使用静态方法创建实现
 
AtomicMarkableReference/AtomicStampedReference

- 前者ReferenceBooleanPair类型的AtomicReference，
ReferenceBooleanPair表示一个对象和boolean标记的pair

- 前者ReferenceIntegerPair类型的AtomicReference，
ReferenceBooleanPair表示一个对象和Integer标记的pair


## AtomicReference:原子性的更新对象引用


AtomicMarkableReference：此API设计的作用非常简单，
原子性的更新对象引用以及其标记位(boolean属性)，
将一个对象引用和一个boolean属性捆绑，内部通过AtomicReference来实现。

比如：AtomicMarkableReference(T reference,boolean initialMark),
此构造函数则构建一个reference并使其具有initialMark的状态。

```java
// 返回绑定的reference。
public V getReference() 

// 返回标记为状态
public boolean isMarked()

// CAS方式更新reference和mark位，只有当reference和mark为都为期望值时，才更新为新值。
public boolean compareAndSet(V expectedReference,V newReference,boolean expectedMark,boolean newMark)
public void set(V newReference, boolean newMark) ：强制设置新的reference和mark位。
```

同样,AtomicStampedReference类和AtomicMarkableReference功能几乎完全一样，
只不过AtomicStampedReference的“标志位”是一个int型，其API方法基本一样。

```java
// 创建对象，使其初始引用和标记位为initialStamp。
AtomicStampedReference(V initialRef, int initialStamp)
```


## AtomicReferenceFieldUpdater<T,V>

此类为抽象类，其提供了内置的实现方式。此类基于反射可以对指定类的指定 volatile 字段进行原子更新。

该类用于原子数据结构，该结构中同一节点的几个引用字段都独立受原子更新控制。
我们之前看到的Atomic的类型API，只能对一种类型数据进行原子性操作。
此类提供了可以针对一个class中多个volatile字段，

进行原子性更行的可能。不过AtomicReferenceFieldUpdater只能适用于类的一个属性。

```java
class Node {  
   private volatile Node left, right;  
   private static final AtomicReferenceFieldUpdater leftUpdater =  
   AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "left");  
   private static AtomicReferenceFieldUpdater rightUpdater =  
   AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "right");  
   Node getLeft() { return left;  }  
   boolean compareAndSetLeft(Node expect, Node update) {  
     return leftUpdater.compareAndSet(this, expect, update);  
   }  
   // ... and so on  
 } 
```

```java
// 使用给定的字段为对象创建和返回一个更新器。需要 Class 参数检查反射类型和一般类型是否匹配。
// 如果指定字段非volatile，将抛出异常。
public static <U,W> AtomicReferenceFieldUpdater<U,W> newUpdater(Class<U> tclass,Class<W> vclass,String fieldName)

```

```java
// 原子性的更新对象的属性值。
boolean compareAndSet(T obj,V expect,V update)
```




