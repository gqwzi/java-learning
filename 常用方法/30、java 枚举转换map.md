
## [原文](https://stackoverflow.com/questions/31112967/how-to-store-enum-to-map-using-java-8-stream-api)

# 枚举转换map 、enum to map using Java 8 stream API


## How to store enum to map using Java 8 stream API

枚举类型
```java
public enum MyEntity{
   Entity1(EntityType.type1,
    ....


   MyEntity(EntityType type){
     this.entityType = entityType;
   }
}
```
转换
```java
private static final Map<EntityType, EntityTypeInfo> lookup =
    Arrays.stream(EntityTypeInfo.values())
          .collect(Collectors.toMap(EntityTypeInfo::getEntityType, e -> e));
```



## [Intellij - can be replaced with method reference](https://stackoverflow.com/questions/44874857/intellij-can-be-replaced-with-method-reference)

```java
.map(obj -> foo.makeSomething(obj))
IntelliJ suggests: "Can be replaced with method reference...". And when I try then:

.map(Foo::makeSomething)
```