## lambda表达式

### lambda的概念

> 无论是我们所说的行为参数化还是函数式编程，一个比较具体的实现都是通过传递方法这种方式来完成。lambda的存在，可以认为是简化这种方式代码量的一个特性。有点类似于简化版的匿名函数。
>
> 它没有名称，但是有参数列表，函数主体，返回类型

### lambda的特性

- 匿名

> lambda不像普通的方法一样有一个明确的名称，代码量少但是所做的工作可没少。

- 函数

> 说lambda是函数，是因为它具备参数列表，函数主体和返回类型，但是它又不同于普通的函数，它不隶属于任何一个类

- 传递

> lambda可以作为参数传递给方法或者存储到变量中

- 简洁

> lambda不像匿名类那样需要写这么多模版代码

### lambda的使用

- 函数式接口

  - 概念

  > 只定义一个抽象方法的接口。一般的，都会在函数式接口中打上@FunctionInterface注解标识这个接口是一个函数式接口。

  - lambda的使用

  > lambda表达式会以内联的方式为函数式接口的抽象方法提供实现，并把整个表达式作为函数式接口的实例。简单来说，就是lambda就等于一个实现了函数式接口抽象方法的具体实现的实例。

- 函数描述符

  - 概念

  > 函数式接口的抽象方法的签名就是lambda表达式的签名，这种抽象方法就称为函数描述符

  - lambda的使用

  > 举个例子，例如Runnable接口，只有一个什么也不接受，什么也不返回的run方法。这个作为lambda表达式的签名就是() -> void这种形式。

### lambda的实践

- java8之前的版本

> ```java
> public static String processFile() {
>     try {
>         BufferedReader br = new BufferedReader(new FileReader("data.txt"));
>         return br.readLine();
>     } catch (Exception e) {
>         e.printStackTrace();
>     }
>     return null;
> }
> ```
>
> 对于资源处理的操作，常见的一种模式就是打开一个资源，做一些处理，然后关闭资源。
>
> 在上述这个实现中，真正会变化的部分就是br.readLine()，所以考虑把这块行为参数化，作为一个方法传进来。对于资源的准备部分，应该考虑重用。

- 第一步--行为参数化

> ```java
> @FunctionalInterface
> public interface BufferedReaderProcessor {
>     String process(BufferedReader bufferedReader);
> }
> ```
>
> 函数式接口，变化部分封装在process方法中，process方法接受一个BufferedReader返回一个String

- 第二步--使用函数式接口来传递行为

> ```java
> public static String processFile(BufferedReaderProcessor bufferedReaderProcessor) {
>     try {
>         BufferedReader br = new BufferedReader(new FileReader("data.txt"));
>         return br.readLine();
>     } catch (Exception e) {
>         e.printStackTrace();
>     }
>     return null;
> }
> ```
>
> processFile方法接受一个函数式接口作为参数，用来改变自身的行为

- 第三步--执行一个行为

> ```java
> public static String processFile(BufferedReaderProcessor bufferedReaderProcessor) {
>     try {
>         BufferedReader br = new BufferedReader(new FileReader("data.txt"));
>         return bufferedReaderProcessor.process(br);
>     } catch (Exception e) {
>         e.printStackTrace();
>     }
>     return null;
> }
> ```
>
> 注意，br.readLine()已经被函数式接口的process方法替代了。

- 第四步--传递lambda

> ```java
> String oneLine = processFile((BufferedReader bufferedReader) -> bufferedReader.readLine());
> String twoLine = processFile((BufferedReader bufferedReader) -> bufferedReader.readLine() + bufferedReader.readLine());
> ```
>
> 到此，实现的效果：
>
> 1、重用了打开资源部分的代码
>
> 2、把可变部分从processFIle中移除出去了，实现方式是函数式编程思想
>
> 3、使用了lambda表达式简化了代码

### 常用的函数式接口

- 函数式接口		函数描述符

  Predicate<T>		T->boolean

  Consumer<T>		T->void

  Function<T,R>		T->R

  Supplier<T>		()->T

  UnaryOperator<T>		T->T

  BinaryOperator<T>		(T,T)->T

  BiPredicate<L,R>		(L,R)->boolean

  BiConsumer<T,U>		(T,U)->void

  BiFunction<T,U,R>		(T,U)->R

### lambda的类型检查、类型推断

- 类型检查

> String oneLine = processFile((BufferedReader bufferedReader) -> bufferedReader.readLine());
>
> 从这个例子来说明lambda的类型检查的背后发生的过程。
>
> 1、lambda的类型是从使用lambda的上下文中推断出来的，例如(BufferedReader bufferedReader) -> bufferedReader.readLine()这个lambda的类型是通过查看processFIle的参数得知lambda的类型是BufferReaderedProcessor，这个类型称为目标类型
>
> 2、知道目标类型后，因为目标类型是个函数式接口，所以找到对应的抽象方法，就能找到对应的函数描述符，在这个例子中，它就是BufferedReader->String
>
> 3、由此跟lambda进行类型匹配，发现无误，说明类型检查没有问题。
>
> 值得注意的一点是，由于目标类型的存在，相同的目标类型可能有不同的函数式接口，这样的情况下是相互兼容的。

- 类型推断

> 简单点的推断就是通过找到目标类型，从而获取到函数描述符，因为知道函数描述符从而知道入参的类型。因此，可以lambda表达式可以省去入参的显示类型定义。

### 方法引用

- 概念

> 方法引用可以理解为lambda表达式的一种快捷写法。但是关注它有一个额外的层面是，语义的体现。即使说java8的特性已经在往声明性编程的方向走，但是在lambda这个局限的场景中，以(BufferedReader bufferedReader) -> bufferedReader.readLine()这个例子为例，它依然在描述是如何调用readLine方法的。使用方法引用，BufferedReader::readLine针对方法的调用，直接声明式的指出某个类型调用某个类型的方法即可。

- 使用场景
  - 指向静态方法的方法引用，例如Integer::parseInt
  - 指向任意类型实例方法的方法引用，例如String::length
  - 指向现有对象的实例方法的方法引用，例如BufferedReader::readLine







