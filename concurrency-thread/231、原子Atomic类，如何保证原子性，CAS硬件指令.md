

# 原子Atomic类，如何保证原子性，CAS硬件指令
 


AtomicInteger，首先有volatile value保证变量的可见性，再借助了CPU级指令CAS保证了原子性。

因为CAS是基于乐观锁的，也就是说当写入的时候，如果寄存器旧值已经不等于现值，说明有其他CPU在修改，那就继续尝试。所以这就保证了操作的原子性。




## 下面以AtomicInteger的实现为例，分析一下CAS是如何实现的。

```java
public class AtomicInteger extends Number implements java.io.Serializable {
    // setup to use Unsafe.compareAndSwapInt for updates
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                (AtomicInteger.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile int value;
    public final int get() {return value;}
}
```
Unsafe，是CAS的核心类，由于Java方法无法直接访问底层系统，需要通过本地（native）方法来访问，
Unsafe相当于一个后门，基于该类可以直接操作特定内存的数据。

变量valueOffset，表示该变量值在内存中的偏移地址，因为Unsafe就是根据内存偏移地址获取数据的。
变量value用volatile修饰，保证了多线程之间的内存可见性。

看看AtomicInteger如何实现并发下的累加操作：

```java
public final int getAndAdd(int delta) {    
    return unsafe.getAndAddInt(this, valueOffset, delta);
}

//unsafe.getAndAddInt
public final int getAndAddInt(Object var1, long var2, int var4) {
    int var5;
    do {
        var5 = this.getIntVolatile(var1, var2);
    } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
    return var5;
}

```

假设线程A和线程B同时执行getAndAdd操作（分别跑在不同CPU上）：

- AtomicInteger里面的value原始值为3，即主内存中AtomicInteger的value为3，根据Java内存模型，

- 线程A和线程B各自持有一份value的副本，值为3。

- 线程A通过getIntVolatile(var1, var2)拿到value值3，这时线程A被挂起。

- 线程B也通过getIntVolatile(var1, var2)方法获取到value值3，运气好，线程B没有被挂起，

- 并执行compareAndSwapInt方法比较内存值也为3，成功修改内存值为2。

- 这时线程A恢复，执行compareAndSwapInt方法比较，发现自己手里的值(3)和内存的值(2)不一致，

- 说明该值已经被其它线程提前修改过了，那只能重新来一遍了。

- 重新获取value值，因为变量value被volatile修饰，所以其它线程对它的修改，线程A总是能够看到，

- 线程A继续执行compareAndSwapInt进行比较替换，直到成功。

- 整个过程中，利用CAS保证了对于value的修改的并发安全，继续深入看看Unsafe类中的compareAndSwapInt方法实现。

```
public final native boolean compareAndSwapInt(Object paramObject, long paramLong, int paramInt1, int paramInt2);
```

Unsafe类中的compareAndSwapInt，是一个本地方法，该方法的实现位于unsafe.cpp中

```java
UNSAFE_ENTRY(jboolean, Unsafe_CompareAndSwapInt(JNIEnv *env, jobject unsafe, jobject obj, jlong offset, jint e, jint x))
  UnsafeWrapper("Unsafe_CompareAndSwapInt");
  oop p = JNIHandles::resolve(obj);
  jint* addr = (jint *) index_oop_from_field_offset_long(p, offset);
  return (jint)(Atomic::cmpxchg(x, addr, e)) == e;
UNSAFE_END
```

先想办法拿到变量value在内存中的地址。
通过Atomic::cmpxchg实现比较替换，其中参数x是即将更新的值，参数e是原内存的值。



一句话总结：首先有volatile value保证变量的可见性，再借助了CPU级指令CAS保证了原子性。


<https://www.jianshu.com/p/fb6e91b013cc>

<http://www.cnblogs.com/Mainz/p/3556430.html>


### CAS硬件指令

CAS指令需要3个操作数，分别是-内存位置 V（在Java中可以简单理解为变量的内存地址）、 
旧的预期值 A（进行运算前从内存中读取的值）、拟写入的值 B（运算得到的值）
当且仅当 V==A 时， 才执行V = B （将B赋给V）,否则将不做任何操作。


- intel手册对lock前缀的说明如下：

确保后续指令执行的原子性。

在Pentium及之前的处理器中，带有lock前缀的指令在执行期间会锁住总线，使得其它处理器暂时无法通过总线访问内存，很显然，这个开销很大。
在新的处理器中，Intel使用缓存锁定来保证指令执行的原子性，缓存锁定将大大降低lock前缀指令的执行开销。

禁止该指令与前面和后面的读写指令重排序。

把写缓冲区的所有数据刷新到内存中。
