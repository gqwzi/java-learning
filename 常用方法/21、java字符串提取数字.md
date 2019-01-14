
## [原文](https://shendixiong.iteye.com/blog/1630418)

# java 字符串提取数字


有的时候，在工作中要过滤掉字符串，或者数字。
在网上找的资料中大多数都用了一个循环实现。
虽然可以达到想要的效果。但是，相对于说会影响一定的效率！

```java
/从字符串中获取数字  
    public  static String getNum(String str) {  
        String dest = "";  
        if (str != null) {  
            dest = str.replaceAll("[^0-9]","");  
  
        }  
        return dest;  
    }  
  
    //从字符串中过滤数字  
    public static String removeNum(String str) {  
        String regEx = "[0-9]";  
        Pattern p = Pattern.compile(regEx);  
        Matcher m = p.matcher(str);  
    //替换与模式匹配的所有字符（即数字的字符将被""替换）  
        return m.replaceAll("").trim();  
    }  
```

