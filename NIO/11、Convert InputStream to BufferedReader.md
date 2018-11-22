
## [原文](https://stackoverflow.com/questions/5200187/convert-inputstream-to-bufferedreader)

# Convert InputStream to BufferedReader

```java
BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
```

java7 以上
```java
BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

```

