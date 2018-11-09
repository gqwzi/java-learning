

## [原文](https://www.jianshu.com/p/d90e4430b0b9)

# Tomcat类加载


现在终于走到正题，想必一定是tomcat并没有完全遵循双亲委派的累加机制，否则不会单独拿出来讲。

首先我们先思考几个问题：

1.如果在一个Tomcat内部署多个应用，甚至多个应用内使用了某个类似的几个不同版本，但它们之间却互不影响。这是如何做到的。

2.如果多个应用都用到了某类似的相同版本，是否可以统一提供，不在各个应用内分别提供，占用内存呢。

至于第一个问题其实上面的讲解已经解答了，就是因为tomcat部署了多个应用，而多个应用都采用自定义的类加载器，
所以即便是同一个类使用不同的类加载机制最终也是不一样的类。

至于第二问题，我首先看看Tomcat的类加载层次：

 

```
     Bootstrap
          |
       System
          |
       Common
       /     \
  Webapp1   Webapp2 ...
```

我们看到webappClassLoader上面有一个common的类加载器，它是所有webappClassLoader的父加载器，
多个应用汇存在公有的类库，而公有的类库都会使用commonclassloader来实现。
这样也就回答了第二个问题；

由此我们也引出了如果不是公有的类呢，这些类就会使用webappClassLoader加载，
而webappClassLoader的实现并没有走双亲委派的模式，这有是为何呢？

原因有两个：

1）加载本类的classloader未知时，为了隔离不同的调用者，即类的隔离，采用了上下文类加载的模式加载类；

2）当前高层的接口在低层去实现，而高层的类有需要低层的类加载的时候，
这个时候，需要使用上下文类加载器去实现（后面会通过JDBC的加载来讲解）

## JDCB的类加载（经典的线程上下文加载器）

```java

private static Connection getConnection(String url,java.util.Properties info,Class caller)throwsSQLException {
            //...
    }

```
由于DriverManger.class是由于jdk里的rt.jar包里面加载的，
而实际调用的是com.mysql.jdbc.Driver的driver该类，而是调用getConnection的方法时候，
下图中的这个方法的到DriverManger这个类是顶级类加载器加载的，这个时候又要启动该类的子类，
所以双亲委派是无法加载该类的，即图二中，caller.getClassLoader是null，
这个时候就会调用 if 里面的线程上下文的加载器，通过上下文加载的方式完成加载，
最好验证该是否可用，完成获取JDBC的连接。




有点跑题，现在回到Tomcat类加载中，我们需要了解到底是采用了双亲委派还是上线文加载模式。
首先我们需要明确的一点就是基础类肯，common类，还是有servlet-api一定用双亲委派模式，
因为这些都是公有的类库，且对于Servlet-api是不允许被重写，也就是说如果你用自己的类加载的话，
会影响到应用内部得到正常运行了，也就是说只有加载app应用的类时候才会引用上下文加载。
下面我们看看上线文加载的类：webappLoader；

在Tomcat启动时，会创建一系列的类加载器，在其主类Bootstrap的初始化过程中，会先初始化classloader，
然后将其绑定到Thread中。

 
 