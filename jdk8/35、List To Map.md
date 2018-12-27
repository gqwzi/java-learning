
## [原文](https://javarevisited.blogspot.com/2016/04/10-examples-of-converting-list-to-map.html)

# java8 List to Map

```java
Map<String, Book> result = books.stream() .collect(Collectors.toMap(book -> book.getISBN, book -> book));

```


```java
Map<String, Book> result = books.stream() .collect(Collectors.toMap(Book::getISBN, b -> b));

```


```java
Map<String, Book> result = choices.stream()
        .collect(Collectors.toMap(Book::getISBN, Function.identity()))
```


## How to convert a List with Duplicates into Map in JDK 8

```java
List cards = Arrays.asList("Visa", "MasterCard", "American Express", "Visa");
Map cards2Length = cards.stream()
                .collect(Collectors.toMap(Function.identity(), String::length));

```

```java
List cards = Arrays.asList("Visa", "MasterCard", "American Express", "Visa"); System.out.println("list: " + cards); 

Map cards2Length = cards.stream() .collect(Collectors.toMap(Function.identity(), String::length, (e1, e2) -> e1)); 
System.out.println("map: " + cards2Length);

```