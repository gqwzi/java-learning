

# 02、String判断是否LATIN1源码实现


JDK9 之后 String 的实现都是判断当前字节是否LATIN1

```java
    final boolean isLatin1() {
        // 如果COMPACT_STRINGS是true 且 coder == 0 就是Latin1
        return COMPACT_STRINGS && coder == LATIN1;
    }
```

COMPACT_STRINGS 默认值是true


```java
    static final boolean COMPACT_STRINGS;

    static {
        COMPACT_STRINGS = true;
    }
```

coder 是一个byte 类型
```java
 private final byte coder;
```
常量 LATIN1 = 0

```java
 
    @Native static final byte LATIN1 = 0;
    @Native static final byte UTF16  = 1;

```

TODO 目前不在 coder 初始值是什么时候赋予的???

