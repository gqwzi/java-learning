
## [原文]()

# Spring的线程池和JDK的线程池的区别？


[API文档](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/concurrent/ThreadPoolTaskExecutor.html)
中很清楚，SpringFrameWork 的 ThreadPoolTaskExecutor 是辅助 JDK 的 ThreadPoolExecutor 的工具类，
它将属性通过 JavaBeans 的命名规则提供出来，方便进行配置。



## Spring中ThreadPoolTaskExecutor的使用 

最常用方式就是做为BEAN注入到容器中,如下代码: 

```xml

<bean id="threadPoolTaskExecutor"  
    class="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor">  
    <property name="corePoolSize" value="10" />  
    <property name="maxPoolSize" value="15" />  
    <property name="queueCapacity" value="1000" />  
</bean>  

```
           
### ThreadPoolExecutor执行器的处理流程: 
- (1)当线程池大小小于corePoolSize就新建线程，并处理请求. 

- (2)当线程池大小等于corePoolSize，把请求放入workQueue中，
池子里的空闲线程就去从workQueue中取任务并处理. 

- (3)当workQueue放不下新入的任务时，新建线程加入线程池，并处理请求，
如果池子大小撑到了maximumPoolSize就用RejectedExecutionHandler来做拒绝处理. 

- (4)另外，当线程池的线程数大于corePoolSize的时候，多余的线程会等待keepAliveTime长的时间，
如果无请求可处理就自行销毁. 


了解清楚了ThreadPoolExecutor的执行流程,
开头提到的org.springframework.core.task.TaskRejectedException异常也就好理解和解决了.ThreadPoolTaskExecutor类中使用的 
就是ThreadPoolExecutor.AbortPolicy()策略,直接抛出异常. 

