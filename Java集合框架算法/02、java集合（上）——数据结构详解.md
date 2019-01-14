
## [原文](https://blog.csdn.net/quinnnorris/article/details/54895024)

# java集合（上）——数据结构详解


当我们要处理一串数据的时候，相比较c++和c中的数组和指针，在Java中我们更为常用的是ArrayList、HashMap等集合数据结构。
c语言对指针的支持成就了他的深度，而Java中多种多样的包装类成就了他的广度。
在java中，我们一般将List、Map、Set等数据结构通归为集合数据结构，这些类都存在于集合类库中。

## （一） 集合接口

### 1.集合的接口和实现分离
与其他的数据结构类库相似的，java的集合类库也采用了这种接口和实现分离的方法。

这种方法的好处是不言而喻的。当你要实例化一个队列时，如果你想去选择链式结构或者循环数组或其他不同的实现方法，
只需为集合接口引用不同的实现类即可。
```java

Queue<String> qe1 = new LinkedList<>();//LinkedList是链表实现队列
Queue<String> qe2 = new ArrayDeque<>();//ArrayDeque是循环数组实现队列

```
同样是Queue的实现类，但采用了不同的方式。

### 2.Collection接口
在集合类库中，最基本的接口是Collection接口。

Collection接口可以理解成集合类库中的树根，所有的其他类都是从之演变出来的。
因为Collection是一个泛型接口，所以在这个泛型接口中java类库的设计者添加了许多的方法，
所有的实现类都必须去实现这些方法。

```java

int size()
//返回此 collection 中的元素数。
//如果此 collection 包含的元素大于 Integer.MAX_VALUE，则返回 Integer.MAX_VALUE。

boolean isEmpty()
//如果此 collection 不包含元素，则返回 true。 

boolean contains(Object o)
//当且仅当此 collection 至少包含一个满足 (o==null ? e==null : o.equals(e)) 的元素 e 时，返回 true。 

Iterator<E> iterator()
//返回在此 collection 的元素上进行迭代的迭代器。
//关于元素返回的顺序没有任何保证,除非此 collection 是某个能提供保证顺序的类实例。 

Object[] toArray()
//返回包含此 collection 中所有元素的数组。
//如果 collection 对其迭代器返回的元素顺序做出了某些保证，那么此方法必须以相同的顺序返回这些元素。 
//返回的数组将是“安全的”，因为此 collection 并不维护对返回数组的任何引用。
//调用者可以随意修改返回的数组。 
//此方法充当了基于数组的 API 与基于 collection 的 API 之间的桥梁。 

boolean add(E e)
//确保此 collection 包含指定的元素。如果此 collection 由于调用而发生更改，则返回 true。
//如果此 collection 不允许有重复元素，并且已经包含了指定的元素，则返回 false。

boolean remove(Object o)
//如果此 collection 包含一个或多个满足 (o==null ? e==null : o.equals(e)) 的元素 e，则移除这样的元素。
//如果此 collection 包含指定的元素（或者此 collection 由于调用而发生更改），则返回 true 。 

boolean containsAll(Collection<?> c)
//如果此 collection 包含指定 collection 中的所有元素，则返回 true。 

boolean addAll(Collection<? extends E> c)
//将指定 collection 中的所有元素都添加到此 collection 中。
//如果在进行此操作的同时修改指定的 collection，那么此操作行为是不确定的。

boolean removeAll(Collection<?> c)
//移除此 collection 中那些也包含在指定 collection 中的所有元素。
//此调用返回后，collection 中将不包含任何与指定 collection 相同的元素。 

void clear()
//移除此 collection 中的所有元素。

boolean retainAll(Collection<?> c)
//仅保留此 collection 中那些也包含在指定 collection 的元素。
//移除此 collection 中未包含在指定 collection 中的所有元素。 

```

以上这些方法将会在所有的集合数据结构中出现，记住他们的作用，无论是哪个数据结构，只要调用他们准没有错。
除此之外真的要赞叹java API编写者的水平，方法功能的介绍用最少的语言来说的滴水不漏，这种超强的概括性，水平之高可见一斑。
尤其像remove中判断的方式，书写简洁美观，包含存在null的情况，真的是非常值得学习。
（特殊的，表不是从Collection接口实现的，而是Map接口）

## （二）Iterator
在创建Collection接口的同时，集合类库也创建了Iterator接口，这个接口的对象是一个迭代器，他会依次遍历集合中所有的元素。
在开始的时候，如果集合是有序的，那么通过Collection接口的iterator方法返回的迭代器对象会在集合起始位置。
```java

Iterator<Integer> it = new Iterator<>();
Iterator<Integer> it = queue.iterator();//通过队列中实现的iterator方法返回迭代器

```

Iterator对象工作的原理是把每个集合中的对象看作一个块，it在这些块之间跳跃。
在开始的时候it在第一个块前（如果是有序集），调用一次next()方法it就会跳到下个块之后，并且跳完之后返回在it前面的块。
如果在开始直接it.remove()会报错，因为remove的原理是删除在it之前的这个块，所以需要先进行next()操作。同理，连续remove两次也是会报错的。

```java

Queue<Integer> qe1 = new LinkedList<>();

qe1.add(null);  
qe1.add(1);
qe1.add(20);
System.out.println(qe1);

Iterator<Integer> it = qe1.iterator();

//-------------error-------------
it.reomve();  //不能直接调用remove()，这时it没有跳过块，it之前没有内容
//-------------------------------


//-------------error-------------
it.next();
it.remove();
it.reomve();  //不能连续调用remove()，it之前的块已被删除，再调用报错
//-------------------------------


//--------------ok---------------
it.next();
it.remove();
it.next();
it.remove();
//-------------------------------

System.out.println(qe1);
//输出：
//[20]


```

上面的例子中值得注意的一点是qe1.add(null);是完全成立的。
基本上所有的集合可以显式的把null作为一个对象传入（除了特殊的集合，比如PriorityQueue…等）。
这样我们也就可以理解API中的：
```java
if(o==null ? e==null : o.equals(e)) //如果集合中有和o相同的e
```

##（三） 链表 LinkedList
在数组以及动态的ArrayList数组存在删除节点代价大的问题后，链表的出现解决了这个问题。
在java中所有的链表其实都是双向的，包含一个前驱结点的引用，存放的对象，指向后一个结点的引用。
```java
List<String> letter = new LinkedList<>(); //链表
```

### 1.ListIterator迭代器
当时用链表的时候也就意味着我们需要进行大量的增添和删除功能。
对于链表这种有序集合，Iterator迭代器无疑是描述位置最好的类，
但是在Iterator中并没有add方法（因为有很多无序集不需要在特定的位置增添元素）所以我们从Iterator中实现了一个子类来进行增删操作——ListIterator。

```java

package Collection;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 * @author QuinnNorris
 * 链表LinkedList的操作，以及ListIterator
 */

public class LinkedListE {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        List<String> letter = new LinkedList<>();

        letter.add(0, "a"); //在索引为0的位置添加元素
        letter.add("b");
        letter.add("c");
        letter.add("d");

        ListIterator li = letter.listIterator(2);//在第3个元素“c”（索引为2）之前放置迭代器
        li.previous();//迭代器向前遍历
        li.remove();//删除刚刚跳过的元素“b”

        li.next();//迭代器向后遍历
        li.remove();//删除刚刚跳过的元素“c”

        li.next();//迭代器向后遍历
        li.set("e");//将刚刚跳过的元素“d”重新设置为“e”

        System.out.println(li.nextIndex());//输出： 2
        System.out.println(li.previousIndex());//输出：1

        System.out.println(letter);//输出：[a,e]

        letter.get(0);//可以使用，但是不要这样做
    }

}
```
ListIterator提供了向前遍历元素的方法previous()，并且提供了让人耳目一新的方法set()。
set这个方法可以重新设置刚刚跳过的那个节点内容，这个方法有些特殊之处，我们以后还会多次用到。

### 2.链表中的get，set方法
需要注意的是，如果你在一个链表中经常去调用get()方法，那么有可能你已经在一条错误的道路上了。
get方法的实现效率非常差，如果不是一条增删需求多于查询需求的表，那么是时候该考虑考虑使用其他的表了。

##（四） 动态数组列表 ArrayList
在上面链表中不适用的set和get方法在ArrayList中是非常有用的。这个类也实现了List接口。
这个列表可能是我们用的相当多的一个列表，在不需要同步的情况下，ArrayList是我们最可靠的小帮手。

##（五） 散列集 HashSet
如果我们只是要将一些元素存放到集合之中，而无须关心他们的次序，
那么我们可以采用散列集来存放这些元素，它可以快速的查找到元素，缺点在于无法控制顺序。

### 1.散列表原理
HashTable是非常出名的数据结构，它的存放对象的原理大概是这样的：

- 将每个对象设置一个散列码（有的通过HashCode()方法）。

- 实现许多条链表数组，将每条这样的数组称为一个桶（bucket）。

- 将对象的散列码与桶的总数取余，得到的余数就是保存这个对象的桶的索引。

- 将这个对象放入这个桶中，如果这个桶此时无对象，则他作为第一个节点，否则跟在最后一个节点后面

- 如果这个桶被占满，发生散列冲突。java会先尝试扩充链表，如果不行，一般采用链表法，继续向后延展。

### 2.散列表中一些预防措施

为了在不浪费空间的情况下尽可能的优化散列表的性能，我们的初始的桶数就要进行估算，
但是事实上我们没法估算…。在java标准类库中采用了默认值为16的桶数。 

除此之外，如果我们在使用的过程中，散列表过于满我们就需要对散列表进行再散列。
再散列的方法很简单，把2的幂数加一，就是以32为桶数重新创建一个散列表，将原来各个桶中元素全部重新计算。
那么我们在什么时候需要再散列呢？在散列表中存在一个装填因子，默认它的值为0.75。也就是说，
如果原来的散列表被装满了75%以上时，这个散列表将会被再散列。

### 3.HashSet
```java

package Collection;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author QuinnNorris
 * 散列集HashSet,要理解存放方法,实际应用中无特别之处
 */
public class HashSetE {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Set<String> hs = new HashSet<>();
        hs.add("master");

        Set<String> sub_hs = new HashSet<>();
        sub_hs.add("sub");

        hs.addAll(sub_hs);//将另一个集合添加进来

        System.out.println(hs);//输出 [sub, master]
    }

}

```
 
散列集HashSet，要理解存放方法，实际应用中无特别之处。如果不需要索引，可用这种集合存放元素。

##（六） 树集 TreeSet

TreeSet和HashSet很像，但是TreeSet却是一种有序集合。 

正如其名，树集是一种树，它将插入这个集合的每个元素都按照一定的规律来排序比较，最后在循环输出的时候，
树集输出的内容是有一定顺序的。也正是因为这个原因，插入树集的元素是必须能比较的，详细的说，
所有插入树集的元素都要实现Comparable接口，否则会报错，这也是能比较的一个前提。

### 1.Comparable接口
我们已经知道，这个接口是插入树集前必须要实现的。Comparable接口定义了一个方法：
```java

public interface Comparable<T>
{
    int compareTo(T other);
}

```
ompareTo方法返回的是一个int类型的数字，如果这个数字大于0，则说明排序时对象在参数之前，反之在参数之后。
在集合中不会出现等于0的情况，毕竟集合是具有单一性，不能一个元素重复存在。

### 2.自定义Comparator接口
毕竟我们写的类不会自己附带一个Comparable接口的实现，那么很多时候，
我们需要披挂上阵自己来定义比较的方法。我们可以创建一个Comparator实现类，
在类中实现compare方法。请注意，我们手动实现的接口是Comparator而不是Comparable。
更好的做法是我们将这个自定的类作为一个参数传入TreeSet的初始化语句中。
```java

ItemComparator comp = new ItemComparator();//ItemComparator是自己写的Comparable的实现类
Set<String> ts = new TreeSet<String>(comp);//将实例comp作为参数传入，TreeSet获得比较方法

```

### 3.匿名内部类
上面的自定义方法固然好，但是那并不是最简洁的写法，在这种情况下，匿名内部类真的起到了非常好的效果。
只要你知道这里的参数是一个实现了compare方法的类对象，你就不会因为内部类的写法而感到很难读懂。

内部类传送门：http://blog.csdn.net/quinnnorris/article/details/54864491
```java

Set<Tree> ts = new TreeSet<Tree>(new 
    Comparator<Tree>(){
        public int compare(Tree a,Tree b){
            return a.index-b.index;
        }
    });

```
Tree是我们自己定义的一个类，自然没有Comparable接口的实现。所以我们在这里实现一下Comparator，
采用的是匿名内部类的方法。事实上，Comparator接口还有equals方法，但是一般的情况下我们并不用去管它。

### 4.TreeSet实现的其他接口
TreeSet本身没有什么更多的便捷方法，但是它实现了很多接口，我们既然要看就看得透彻一些，来把这些接口也学习一下。

```java

SortedSet<String> ts = new TreeSet<>();
//SortedSet接口

Comparator<? super E> comparator()
// 返回对此 set 中的元素进行排序的比较器。如果此 set 使用其元素的自然顺序，则返回 null。

E first()
//返回此 set 中当前第一个（最低）元素。 

E last()
//返回此 set 中当前最后一个（最高）元素。 

NavigableSet<String> ts = new TreeSet<>();
//NavigableSet接口

ceiling(E e) 
//返回此 set 中大于等于给定元素的最小元素；如果不存在这样的元素，则返回 null。 

Iterator<E> descendingIterator() 
//以降序返回在此 set 的元素上进行迭代的迭代器。 

 E floor(E e) 
//返回此 set 中小于等于给定元素的最大元素；如果不存在这样的元素，则返回 null。 

 E higher(E e) 
//返回此 set 中严格大于给定元素的最小元素；如果不存在这样的元素，则返回 null。 

 Iterator<E> iterator() 
//以升序返回在此 set 的元素上进行迭代的迭代器。 

 E lower(E e) 
//返回此 set 中严格小于给定元素的最大元素；如果不存在这样的元素，则返回 null。 

 E pollFirst() 
//获取并移除第一个（最低）元素；如果此 set 为空，则返回 null。 

 E pollLast() 
//获取并移除最后一个（最高）元素；如果此 set 为空，则返回 null。

```

实际上，NavigableSet实现了SortedSet，如果想要使用上面的方法用NaviagableSet引用即可。

##（七） 双端队列 ArrayDeque
顾名思义，有两个端头的队列就叫做双端队列，而deque的含义也正是“double ended queue”。
双端队列可以在两端进行增删元素操作，但是不能在队列中间添加元素。

在java类库中，Deque<T>实现了Queue接口，而ArrayDeque实现了Deque接口。
```java

package Collection;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 
 * @author QuinnNorris
 * 双端队列ArrayDeque的基本操作
 */
public class ArrayDequeE {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Deque<String> ad = new ArrayDeque<>();//默认构造一个大小为16的双端队列

        ad.add("a");//将一个对象插入双端队列的末尾
        ad.addFirst("b");//将一个对象插入双端队列的头部
        ad.addLast("c");//将一个对象插入双端队列的末尾

        String first = ad.pollFirst();//获取并移除双端队列的第一个元素，pollLast()功能相反

        String second = ad.getLast();//获取双端队列的最后一个元素，getFrist()功能相反

    }
}

```

这个类的功能也很明确就是在头部尾部能够做文章，偶尔或许会用到吧。值得一提的是，因为这个实现类继承了很多队列的接口，
所以ArrayDeque里面有很多功能相同的方法，我们看着用就可以，作用是差不多的。

##（八） 优先级队列 PriorityQueue
在操作系统中，CPU要处理很多进程任务，有个管理这些进程的算法就叫做优先级队列算法。
java中的优先级队列和这个很像。在优先级队列中，元素只要add进去就不用管了，因为这个队列就是一个堆（heap），
实质上是一个AVL二叉树。在这个二叉树中他总是按照compareTo方法将元素排序，优先级低的放在前面。

```java

package Collection;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * 
 * @author QuinnNorris
 * 优先级队列PriorityQueue基本用法
 */
public class PriorityQueueE {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Queue<String> pq = new PriorityQueue<>();
        pq.add("a");
        pq.offer("b");//向优先级队列中添加一个对象，和add方法相同。
        pq.offer("c");

        String head = pq.peek();//获取此列表的头
        System.out.println(head);//输出： a

        System.out.println(pq);//输出：[a, b, c]

        pq.remove();//移除队列中优先级最小的元素,也就是第一个元素，也可用参数表示删除什么元素

        System.out.println(pq);//输出： [b, c]
    }

}

```
 
PriorityQueue的方法也是出奇的简单，只有几种特殊的方法，毕竟二叉树自己内部就进行了自动调整，不需要我们做太多。
与TreeSet一样，我们也可以通过自己创建实现了Comaprator接口的类对象来控制排序的原则。
在PriorityQueue中值得注意的是，如果remove方法没有参数，那么默认会删除根节点的元素（第一个元素）。

##（九） 散列表 HashMap

### 1.映射表
有的时候，我们要查找一个元素，但是并不想根据它的索引数字来查找。
更有意义的，我们想用除了数字之外的类似String，Double，或者其他对象来查找这个值，
那么这就涉及到一对数据。在java和很多语言中都实现了这种数据结构，通过一对键值对（key-value对）来存放数据，这就是映射表。

### 2.散列表特性
散列表和散列集的特性是基本差不多的，都是通过桶来储存。和散列集区别的是，
它是根据键的hashcode来进行分配，散列集就比较简单。值得一提的是，键和值都可以为null，
HashMap不是同步的，在多线程并发的情况下需要更多的保护操作。

```java

package Collection;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author QuinnNorris
 * HashMap的基本操作
 */
public class HashMapE {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Map<String,String> hm = new HashMap<>();
        Map<String,String> hmc = new HashMap<>(20,0.8F);//可以有两个参数，第一个表示桶的个数，第二个表示装填因子

        hm.put("key", "value");//存放键值对

        hm.get("key");//获取参数键的对应值，如果没有这个键则返回null

        hm.containsKey("key");//返回布尔值true;表示包含这个键

        hm.containsValue("value");//返回布尔值true，表示包含这个键

        hm.remove("key");//根据键来移除键值对

    }

}

```

在HashMap中，可以根据key来获取value的值，但是如果要反向根据value来获取key的值时则需要用到其他的手法，这个实现方法我们等到以后再讨论。

## （十） 树表 TreeMap
树表是java集合类库提供的第二种表，这种表的特点全写在名字上了，树+表。具体有以下这些特性：

- 树会根据Comparable类的compareTo方法作为默认比较器，将传入元素排序

- 可以自己实现Comaprator类的compare方法作为比较器，将对象传入参数中

- 比较的变量是键key不是值value

- 树表比散列表稍微慢一些，不会慢太多，在需要有序输出的时候要用树表

## （十一） 总结
一万多字的内容概括的介绍了所有在集合类库常用的几种类。这些类实现的原理不同，功能不同，大概可以分成List、Map、Set三大种，
除了Map是Map接口实现的，其他的都是Collection接口实现的，在下面我们可以继续看一些集合框架，看java是怎么把这些类组合在一起的。
 