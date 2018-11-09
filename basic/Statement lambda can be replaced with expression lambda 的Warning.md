

## [原文](https://blog.csdn.net/qq_32916805/article/details/78569931)

# Statement lambda can be replaced with expression lambda 的Warning

warning的全文如下

> Statement lambda can be replaced with expression lambda less… (Ctrl+F1) 
This inspection reports lambda expressions with code block bodies when expression-style bodies can be us

warning的地方在这里

```java

recyclerViewAdapter.setOnItemClicListener((v ,id) -> { 
checkChoosed(id); 
});


```

只需要改成这样


```java 
recyclerViewAdapter.setOnItemClicListener((v ,id) -> checkChoosed(id) );

```

就OK了,既然使用了lamda表达式，完全可以更简单一点