

# 03、JDK9中的AbstractStringBuilder实现方式

在JDK9中，AbstractStringBuilder的底层上也是用了byte[]

```java

    /**
     * The value is used for character storage.
     */
    byte[] value;

    /**
     * The id of the encoding used to encode the bytes in {@code value}.
     */
    byte coder;

    /**
     * The count is the number of characters used.
     */
    int count;
    
    AbstractStringBuilder(int capacity) {
        if (COMPACT_STRINGS) {
            value = new byte[capacity];
            coder = LATIN1;
        } else {
            value = StringUTF16.newBytesFor(capacity);
            coder = UTF16;
        }
    }
    
```


AbstractStringBuilder的底层上使用byte[]，
意味着StringBuilder和StringBuffer用的也是byte[]，为什么这么说，
因为StringBuilder和StringBuffer继承自AbstractStringBuilder。

  建议读者阅读源码。

## Reference:

- JDK源码

- <https://stackoverflow.com/questions/44178432/difference-between-compact-strings-and-compressed-strings-in-java-9/44179353>

