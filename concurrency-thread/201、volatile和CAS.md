

# volatile和CAS

java.util.concurrent包都中的实现类都是基于volatile和CAS来实现的。
尤其java.util.concurrent.atomic包下的原子类。

## 简单介绍下volatile特性： 

1. 内存可见性（当一个线程修改volatile变量的值时，另一个线程就可以实时看到此变量的更新值） 

2. 禁止指令重排（volatile变量之前的变量执行先于volatile变量执行，volatile之后的变量执行在volatile变量之后）


 ## CAS 
  
  CAS（Compare and Swap），即比较并替换，实现并发算法时常用到的一种技术。
  
  CAS的思想很简单：三个参数，一个当前内存值V、旧的预期值A、即将更新的值B，
  当且仅当预期值A和内存值V相同时，将内存值修改为B并返回true，否则什么都不做，并返回false。
