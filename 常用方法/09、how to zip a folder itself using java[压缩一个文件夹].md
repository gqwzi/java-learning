
## [原文](https://stackoverflow.com/questions/15968883/how-to-zip-a-folder-itself-using-java)

# how to zip a folder itself using java

如何压缩一个文件夹
 
Have you tried Zeroturnaround Zip library? 
It's really neat(牛逼)! Zip a folder is just a one liner:

```java
ZipUtil.pack(new File("D:\\reports\\january\\"), new File("D:\\reports\\january.zip"));
```