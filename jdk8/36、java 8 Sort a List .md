

## [原文](https://stackoverflow.com/questions/33487063/java-8-sort-list-of-objects-by-attribute-without-custom-comparator)

# java 8 Sort a List

jdk8 排序
排序


```java
list.sort(Comparator.comparing(a -> a.attr));
```
  
  
```java
list.sort(Comparator.comparing(AnObject::getAttr));
```  

如果值里面有 null 的情况

```java
 list.sort(Comparator.comparing(a -> a.attr, Comparator.nullsFirst(Comparator.naturalOrder())));
```


 