

## [https://www.jianshu.com/p/1712ef4f2717]

# cglib动态代理


JDK的动态代理机制只能代理实现了接口的类，而不能实现接口的类就不能实现JDK的动态代理，cglib是针对类来实现代理的，他的原理是对指定的目标类生成一个子类，并覆盖其中方法实现增强，但因为采用的是继承，所以不能对final修饰的类进行代理。
cglib实现动态代理的方法和JDK动态代理类似

 
 ## 实现MethodInterceptor接口
 
 获得代理对象
 
```java

 public Object getInstance(Object target){
     this.target = target;
     Enhancer enhancer = new Enhancer();
     enhancer.setSuperclass(this.target.getClass());
     //设置回调方法
     enhancer.setCallback(this);
     //创建代理对象
     return enhancer.create();
 }
```
 
 设置回调方法
 
```java
 @Override
 public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
     System.out.println("UserFacadeProxy.intercept begin");
     methodProxy.invokeSuper(o,objects);
     System.out.println("UserFacadeProxy.intercept end");
    return null;
 }
``` 

##  Spring AOP原理
 java动态代理是利用反射机制生成一个实现代理接口的匿名类，
 在调用具体方法前调用InvokeHandler来处理。
 
 而cglib动态代理是利用asm开源包，
 对代理对象类的class文件加载进来，通过修改其字节码生成子类来处理。
 
## SpringAOP动态代理策略是：

- 1、如果目标对象实现了接口，默认情况下会采用JDK的动态代理实现AOP 

- 2、如果目标对象实现了接口，可以强制使用CGLIB实现AOP 

- 3、如果目标对象没有实现了接口，必须采用CGLIB库，spring会自动在JDK动态代理和CGLIB之间转换
 。