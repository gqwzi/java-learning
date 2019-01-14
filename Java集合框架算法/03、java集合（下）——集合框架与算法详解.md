


## [原文1](https://blog.csdn.net/QuinnNorris/article/details/54969126)

## [原文2](https://blog.csdn.net/wangdingqiaoit/article/details/14148139)

#  java集合（下）——集合框架与算法详解


集合框架提供了一些诸如排序，查找，打散顺序(Shuffling)，逆置，旋转，取最大值，取最小值等基本算法，
还可以使用集合框架中的接口实现自己的算法。

## 1.排序算法

## 1.1 java中对象排序的方式

java中的sort排序采用稳定的归并排序算法。

要对一个集合进行排序有两种方法:

### 1) 实现Comparable接口，进行自然排序。

Comparable该接口声明有方法:

int compareTo(T o),利用该方法对元素进行排序，这称为自然排序(natural ordering) 。
对于没有实现Comparable接口的类调用Collections.sort（）或者Arrays.sort()方法均会抛出ClassCastException异常。

- [例子](/src/main/java/space/pankui/source/java/uti/CollectionsDemo001.java)

### 2)构造Comparators比较器，自定义排序规则。

Comparators接口声明有一个比较方法：
```java

public interface Comparator<T> {
    int compare(T o1, T o2);
}

```
通过像集合类或者sort方法传递一个Comparator即可实现自己所需要的排序。


## 2.查找算法

java查找排序采用二分查找，要求必须是有序的顺序存贮的列表，否则影响查找结果和查找效率。
   
查找成功时，返回该元素在列表中的索引;当查找失败时，返回的索引并非无用，
它恰好给出了插入该元素的一个参考，查找失败时，元素插入位置为：-pos-1,如下:
   
   
```java
   
   int pos = Collections.binarySearch(list, key);
   if (pos < 0)
      l.add(-pos-1, key);

```


