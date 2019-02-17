

#  Memory Barriers 内存屏障

<http://www.cs.otago.ac.nz/cosc440/readings/HWMB.pdf>

Memory Barriers: a Hardware View for Software Hackers，对于Memory Barriers得到了更加深入的理解。

Cache本身的更新是遵守MESI（Modified，Exclusive，Shared，Invalid）协议的。CPU之间的Cache信息更新通过消息传递来完成。

但是现在CPU的设计中，在Cache之外加入了Store Buffer和Invalidate Queue。Store Buffer的加入，
使得CPU对某内存单元的更新不能马上反映到Cache中；Invalidate Queue的存在，
使得其他CPU对Cache的invalidate操作不能马上反映到Cache中。Store Buffer和Invalidate Queue提高了性能，
但是也就导致了Cache的不一致。

因此需要引入Memory Barriers。Store Buffer和Invalidate Queue应该分别对应使用wmb和rmb。当然直接使用通用mb也是可以的。

Roughly speaking, a “rmb” marks only the invalidate queue and a “wmb” marks only the store buffer, 
while a “mb” does both.

一般来说，Memory Barriers应该配对使用，比如说一方使用了rmb另外一方对应使用wmb。在Linux内核中，
还存在着Data Dependence Memory Barrier，这是一个较弱的rmb。
具体见Linux内核代码的Documentation/memory-barriers.txt。

