

## [原文](https://www.concretepage.com/java/jdk-8/java-8-runnable-and-callable-lambda-example-with-argument)

#  Java 8 Runnable and Callable Lambda Example with Argument

java 8 Runnable 和 Callable 使用Lambda 表达式例子

On this page we will provide Java 8 Runnable and Callable lambda example with argument. 
In java 8 Runnable and Callable both interface have been annotated by @FunctionalInterface.
We can implement run() and call() method using lambda expression. 

Here on this page we will also provide how to pass arguments to Runnable and Callable methods.

Java 8 Runnable Lambda Example with Argument

Java 8 supports lambda expression. In java 8 Runnable interface has been annotated with @FunctionalInterface. 
Now we can create Runnable instance using lambda expression.
```java

Runnable r = () -> System.out.println("Hello World!");
Thread th = new Thread(r);
th.start(); 

```

The above code is equivalent to below code.

```java
Runnable r = new Runnable() {
   @Override
   public void run() {
	System.out.println("Hello World!");
   }
};
Thread th = new Thread(r);
th.start(); 

```
In case we need to write more than one line of code inside run() method,
 we can do using lambda expression as given below.


```java
Runnable r = () -> {
	Consumer<Book> style = (Book b) -> System.out.println("Book Id:"+b.getId() + ", Book Name:"+b.getName());
	list.forEach(style);
}; 

```

To pass the argument to our run() method we should use final modifier.


```java
final List<Book> list =  Arrays.asList(new Book(1, "Ramayan"), new Book(2, "Mahabharat"));
Runnable r = () -> {
	Consumer<Book> style = (Book b) -> System.out.println("Book Id:"+b.getId() + ", Book Name:"+b.getName());
	list.forEach(style);
}; 

```
Now find the complete example of Java 8 Runnable with lambda expression using Thread class. 
Java8RunnableDemo.java

```java
package com.concretepage.runnable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import com.concretepage.Book;
public class Java8RunnableDemo {
	public static void main(String[] args) {
		final List<Book> list =  Arrays.asList(new Book(1, "Ramayan"), new Book(2, "Mahabharat"));
		Runnable r1 = () -> list.forEach(Book::print);
		Thread th1 = new Thread(r1);
		th1.start();
		Runnable r2 = () -> {
			Consumer<Book> style = (Book b) -> System.out.println("Book Id:"+b.getId() + ", Book Name:"+b.getName());
			list.forEach(style);
		};
		Thread th2 = new Thread(r2);
		th2.start();
	}
} 

```
Book.java

```java
package com.concretepage;
public class Book {
        public int id;
        public String name;
        public Book(int id, String name){
            this.id = id;
            this.name = name;
        }
        public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void print(){
               System.out.println("id:"+id + ", Name:"+name);
        }
} 

```
Find the output.

```
id:1, Name:Ramayan
Book Id:1, Book Name:Ramayan
id:2, Name:Mahabharat

Book Id:2, Book Name:Mahabharat 

```
Find the sample code to run the Runnable instance using ExecutorService. 
Java8RunnableDemoExecutor.java

```java
package com.concretepage.runnable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import com.concretepage.Book;
public class Java8RunnableDemoExecutor {
	public static void main(String[] args) {
		final List<Book> list =  Arrays.asList(new Book(1, "Ramayan"), new Book(2, "Mahabharat"));
		ExecutorService service =  Executors.newFixedThreadPool(2);
		Runnable r1 = () -> list.forEach(Book::print);
		service.execute(r1);
		Runnable r2 = () -> {
			Consumer<Book> style = (Book b) -> System.out.println("Book Id:"+b.getId() + ", Book Name:"+b.getName());
			list.forEach(style);
		};
		service.execute(r2);
	}
}

```
Find the output.

```
id:1, Name:Ramayan
id:2, Name:Mahabharat
Book Id:1, Book Name:Ramayan
Book Id:2, Book Name:Mahabharat

```
## Java 8 Callable Lambda Example with Argument

Callable<V> interface has been introduced in Java 5 where V is a return type. 
In Java 8, Callable interface has been annotated with @FunctionalInterface. 
Now in java 8, we can create the object of Callable using lambda expression as follows.
```java

Callable<Integer> callableObj = () -> { return 2*3; }; 

```
The above code is equivalent to below code snippet.
```java
Callable<Integer> callableObj = new Callable<Integer>() {
	@Override
	public Integer call() throws Exception {
		return 2*3;
	}
}; 
```
To pass the argument to our call() method we should use final modifier.
```java
final int val = 10; 
Callable<Integer> callableObj = () -> { return 2*val; }; 

```
Now find the complete example of Callable with lambda expression using ExecutorService. 
Java8CallableDemo.java

```java
package com.concretepage.callable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
public class Java8CallableDemo {
	public static void main(String[] args) {
		final List<Integer> integers =  Arrays.asList(1,2,3,4,5);
		Callable<Integer> callableObj = () -> {
			int result = integers.stream().mapToInt(i -> i.intValue()).sum();
			return result;
		};
		ExecutorService service =  Executors.newSingleThreadExecutor();
		Future<Integer> future = service.submit(callableObj);
		Integer result=0;
		try {
			result = future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("Sum = "+result);
	}
} 

```
Find the output.

```
Sum = 15

```