
## [原文](https://stackoverflow.com/questions/2271926/how-to-read-a-file-from-a-jar-file)

# How to read a file from a jar file?

java 读取jar 里面的文件

You can't use File, since this file does not exist independently on the file system. 
Instead you need getResourceAsStream(), like so:

```java
// 如果 resource 里面还有文件夹则 是 /dir/1.txt
InputStream in = getClass().getResourceAsStream("/1.txt");

BufferedReader input = new BufferedReader(new InputStreamReader(in));
```


If your jar is on the classpath:
```java

InputStream is = YourClass.class.getResourceAsStream("1.txt");

```
If it is not on the classpath, then you can access it via:


```java
URL url = new URL("jar:file:/absolute/location/of/yourJar.jar!/1.txt");
InputStream is = url.openStream();

```