

# CAS缺点及其ABA问题

## CAS缺点

CAS存在一个很明显的问题，即ABA问题。

问题：如果变量V初次读取的时候是A，并且在准备赋值的时候检查到它仍然是A，那能说明它的值没有被其他线程修改过了吗？

如果在这段期间曾经被改成B，然后又改回A，那CAS操作就会误认为它从来没有被修改过。

针对这种情况，java并发包中提供了一个带有标记的原子引用类AtomicStampedReference，它可以通过控制变量值的版本来保证CAS的正确性

## 如何解决ABA问题

用AtomicStampedReference/AtomicMarkableReference

各种乐观锁的实现中通常都会用版本戳version来对记录或对象标记，避免并发操作带来的问题，
在Java中，AtomicStampedReference也实现了这个作用，
它通过包装类Pair[E,Integer]的元组来对对象标记版本戳stamp，从而避免ABA问题。


这样是不是就是说AtomicInteger存在ABA问题，根本就不能用了；肯定是可以用的，
AtomicInteger处理的一个数值，所有就算出现ABA问题问题，也不会有什么影响；
但是如果这里是一个地址（地址被重用是很经常发生的，一个内存分配后释放了，再分配，很有可能还是原来的地址），
比较地址发现没有问题，但其实这个对象早就变了，这时候就可以使用AtomicStampedReference来解决ABA问题。

