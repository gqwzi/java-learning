
## [原文](https://www.jianshu.com/p/0ca6160a6db4)

# Java @FunctionalInterface 注解

我们常用的一些接口Callable、Runnable、Comparator等在JDK8中都添加了@FunctionalInterface注解。

## 作用：
表明这是一个函数接口（对编译器），如果不是一个函数式接口，编译器会报错；
反之，如果已经是一个函数式接口，有没有此注解都不造成影响。
 

## 相关知识点：

- 函数式接口：英文，Functional Interface；
  - 该接口里面有且只有一个抽象方法；
  - 该接口可以被隐式转换为lambda表达式；
  - 该接口可以现有的函数友好地支持 lambda。

- 抽象方法：
  - 指一些只有方法声明，而没有具体方法体的方法。抽象方法一般存在于抽象类或接口中。
 
 