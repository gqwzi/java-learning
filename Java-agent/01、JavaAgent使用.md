
## [原文](https://www.jianshu.com/p/3bd123915216)

# Java Agent 使用

## 一、简单介绍

Java agent 是JDK 1.5 以后引入的，也可以叫做Java代理

java agent是jvm插件或者叫做代理，她是运行在main方法之前，她内定的方法名称叫premain。

Java agent 是运行在 main方法之前的拦截器，它内定的方法名叫 premain ，
也就是说先执行 premain 方法然后再执行 main 方法。

那么如何实现一个 Java agent 呢？

### 1、实现 premain 方法

```java

package chinaunicom.softwareri.javaagenttest;

import java.lang.instrument.Instrumentation;

public class MyAgent {
        /**
        * 该方法在main方法之前运行，与main方法运行在同一个JVM中
        * 并被同一个System ClassLoader装载
        * 被统一的安全策略(security policy)和上下文(context)管理
        */
    public static void premain(String agentOps, Instrumentation inst) {
        System.out.println("=========premain方法执行========");
        System.out.println(agentOps);
    }

        /**
        * 如果不存在 premain(String agentOps, Instrumentation inst) 
        * 则会执行 premain(String agentOps)
        */
    public static void premain(String agentOps) {
        System.out.println("=========premain方法执行2========");
        System.out.println(agentOps);
    }
}

```

## 2、打包
打包这步要完成的内容是在 META-INF/MANIFEST.MF 中设置如下内容:
```java

Manifest-Version: 1.0
Premain-Class: com.shanhy.demo.agent.MyAgent
Can-Redefine-Classes: true

```
你可以自己生成这个文件,然后在打包的时候使用你的文件

- 如果是 eclipse 中 export，则选择使用你的文件

- 如果是 maven, 那么 maven-jar-plugin 插件也提供了使用你的文件的方式生成 MANIFEST.MF

- 如果是自己生成文件要注意 一共是四行，第四行是空行，还有就是冒号后面的一个空格

我使用的是使用设置 maven-jar-plugin 插件的 manifestEntries 属性, 如下所示
```java

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.0.2</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                        </manifest>
                        <manifestEntries>
                            <Premain-Class>
                                chinaunicom.softwareri.javaagenttest.MyAgent
                            </Premain-Class>
                            <Can-Redefine-Classes>
                                true
                            </Can-Redefine-Classes>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
            
```
假设我们打包代码为 myagent.jar

## 3、使用
这里为了测试，假设我们实现了一个简单的 myapp.jar,
其Main-Class的main方法为:
```java

public static void main(String[] args) throws Exception {
    System.out.println("===========main方法执行=============");
}

```
然后执行

```java
java -javaagent:./myagent.jar=Hello1 -javaagent:./myagent.jar=Hello2 \
-jar ./myapp.jar \
-javaagent:./myagent.jar=Hello3

```
输出结果:
```java

=========premain方法执行========
Hello1
=========premain方法执行========
Hello2
=========main方法执行========

```
命令格式为:

> java -javaagent:your_agent_jar1=parameter1 -javaagent:your_agent_jar2=parameter2 -jar:your_jar

parameter对应的就是 premain(String agentOps, Instrumentation inst) 中的 agentOps
如果没有使用 = 提供，那么该参数就是 null

> 你可能已经注意到, 放在 -jar 后面的 -javaagent 没有执行

### 4、最后
至此，我们会使用 javaagent 了，但是单单看这样运行的效果，好像没有什么实际意义

那么我们可以用 javaagent 做什么呢？

## 二、java agent 应用
其实我们使用  -javaagent: 加载的是 instrument agent,
instrument agent实现了 Agent_OnLoad 和 Agent_OnAttach 两个方法,
也就是说 agent既可以在启动时加载，
也可以在运行时动态加载(在 Java SE 6 中实现)

也就是说我们使用  -javaagent: 是在 JVM 启动时 加载,

同样在JVM运行时也可以动态加载:
通过 JVM Attch机制实现, 通过发送load命令来加载agent

```java

VirtualMachine vm = VirtualMachine.attach(pid); 
vm.loadAgent(agentPath, agentArgs); 

```
动态加载的 agent , 需要实现 agentmain 方法, 
并且在 manifest 文件里面设置 Agent-Class 来指定包含 agentmain 函数的类

无论是 在启动时 还是 在运行时 加载 instrument agent ，
主要都是监听 ClassFileLoadHook 事件, 并最终调用 premain 方法 / agentmain 方法
ClassFileLoadHook 这个事件 发生在读取字节码文件之后，
这样就可以对原来的字节码做修改,

但是要如何实现呢?

premain(String agentOps, Instrumentation inst) 或 
agentmain(String agentOps, Instrumentation inst) 中的
 Instrumentation inst 参数有几个方法可以帮助你实现这些操作.

### addTransformer
inst.addTransformer(ClassFileTransformer transformer) 
方法可以将你的自定义的 Transformer 注册到 TransformerManager 里

实现 ClassFileTransformer 接口中的 transform() 方法, 利用反射、字节码等机制, 
可以实现类似于AOP的效果，可以直接将类的字节码全部替换，也可以在原来的基础上 进行修改后返回

### transform方法示例
```java

public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
    // 简单的判断是否是 要进行转换的类,
    // 如果不是,  返回 null ，应该是表示不进行转换的 意思
    // 如果是, 则使用新的类定义文件 进行替换
    // 复杂一些的逻辑 可以使用 字节码技术 在原来的基础上进行更改
    if (!className.equals("TransClass")) { 
        return null; 
    } 
    return getBytesFromFile(classNumberReturns2); 

} 

```
### redefineClasses
除了可以使用 inst.addTransformer() 之外, 还可以使用
```java

inst.redefineClasses(new ClassDefinition[] { def });

```
每个 ClassDefinition 定义一个 类的转换关系

### retransformClasses
一般在 agentmain 函数中 仍然是 addTransformer(),
此外 还需要使用使用 inst.retransformClasses(TransClass.class);
表示要对 TransClass 重新进行 Transform

例子:
```java

 public class AgentMain { 
    public static void agentmain(String agentArgs, Instrumentation inst) 
            throws ClassNotFoundException, UnmodifiableClassException, 
            InterruptedException { 
        inst.addTransformer(new Transformer (), true); 
        inst.retransformClasses(TransClass.class); 
        System.out.println("Agent Main Done"); 
    } 
 }
 
```

