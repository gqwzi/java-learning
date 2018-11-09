
## [原文](https://www.javaquery.com/2016/10/how-to-sum-values-from-list-set-and-map.html)

# How to sum values from List, Set and Map using stream in Java 8?

Following examples shows how you can use java 8 Stream api to do summation of Integer,
Double and Long in List, Set and Map.

Source code (Item.java)

```java

public class Item {

    private Long id;
    private String name;
    private Double price;
    
    public Item(String name, Double price) {
        this.name = name;
        this.price = price;
    }
    
    /* getter - setter */

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

```

List \
Summation of Integers in List. \
Summation of Price from List of beans(Item).

Source code (ListStreamSum.java)
```java
import com.javaquery.bean.Item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Summation of element in List using Stream api in java 8.
 *
 * @author javaQuery
 * @date 17th October, 2016
 * @Github: https://github.com/javaquery/Examples
 */
public class ListStreamSum {

    public static void main(String[] args) {
        /* Summation of Integers in List */
        List<Integer> integers = new ArrayList<>(Arrays.asList(10, 20, 30));

        Integer integerSum = integers.stream().mapToInt(Integer::intValue).sum();
        System.out.println("summation: " + integerSum);

        /* Summation when you have list of beans */
        Item motoG = new Item("MotoG", 100.12);
        Item iPhone = new Item("iPhone", 200.12);

        List<Item> listBeans = new ArrayList<>(Arrays.asList(motoG, iPhone));

        Double doubleSum = listBeans.stream().mapToDouble(Item::getPrice).sum();
        System.out.println("summation: " + doubleSum);
    }
}

```
Output

``` 
summation: 60
summation: 300.24
Set
Summation of Integers in Set.
Summation of Price from Set of beans(Item).
```


Source code (SetStreamSum.java)

```java
import com.javaquery.bean.Item;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Summation of element in Set using Stream api in java 8.
 *
 * @author javaQuery
 * @date 17th October, 2016
 * @Github: https://github.com/javaquery/Examples
 */
public class SetStreamSum {

    public static void main(String[] args) {
        /* Summation of Integers in Set */
        Set<Integer> integers = new HashSet<>(Arrays.asList(10, 20, 30));

        Integer integerSum = integers.stream().mapToInt(Integer::intValue).sum();
        System.out.println("summation: " + integerSum);

        /* Summation when you have set of beans */
        Item motoG = new Item("MotoG", 100.12);
        Item iPhone = new Item("iPhone", 200.12);

        Set<Item> listBeans = new HashSet<>(Arrays.asList(motoG, iPhone));

        Double doubleSum = listBeans.stream().mapToDouble(Item::getPrice).sum();
        System.out.println("summation: " + doubleSum);
    }
}

```
Output
```java

summation: 60
summation: 300.24

```

Map \
Summation of Integers in Map as a value. \
Summation of Integers in List as a value. \
Summation of Price from List of beans(Item) in Map.

Source code (MapStreamSum.java)

```java

import com.javaquery.bean.Item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Summation of element in Map using Stream api in java 8.
 *
 * @author javaQuery
 * @date 17th October, 2016
 * @Github: https://github.com/javaquery/Examples
 */
public class MapStreamSum {

    public static void main(String[] args) {
        /* Summation of Integers in Map */
        Map<String, Integer> integers = new HashMap<>();
        integers.put("A", 10);
        integers.put("B", 20);
        integers.put("C", 30);

        Integer integerSum = integers.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("summation: " + integerSum);

        /* Summation when you have List/Set in Map */
        Map<String, List<Integer>> listInMap = new HashMap<>();
        listInMap.put("even_numbers", new ArrayList<>(Arrays.asList(2, 4, 6)));

        integerSum = listInMap.values().stream()
                .flatMapToInt(list -> list.stream().mapToInt(Integer::intValue))
                .sum();
        System.out.println("summation: " + integerSum);

        /* Summation when you have List/Set of beans in Map */
        Item motoG = new Item("MotoG", 100.12);
        Item iPhone = new Item("iPhone", 200.12);

        List<Item> items = new ArrayList<>(Arrays.asList(motoG, iPhone));

        Map<String, List<Item>> itemMap = new HashMap<>();
        itemMap.put("items", items);

        Double doubleSum = itemMap.values().stream()
                .flatMapToDouble(list -> list.stream().mapToDouble(Item::getPrice))
                .sum();
        System.out.println("summation: " + doubleSum);
    }
}


```
Output
``` 
summation: 60
summation: 12
summation: 300.24
```