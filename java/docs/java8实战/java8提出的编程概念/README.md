## JAVA8提出的编程概念

### 流处理

- 概念

> 流是一系列的数据项，程序可以从输入流中一个个读取数据项，然后以相同的方式写入到输出流中。一个程序的输出流很可能就是一个程序的输入流。

- 好处

> 1、编程的思路从通过迭代的方式一项一项的处理数据变成把一个流变成另一个流。抽象的层次更高，声明式的编程。关注的点是我要做什么，而不是我要怎么做。
>
> 2、java8提供了并行处理流的方式，不需要程序员手动去使用线程。

- 示例代码

> 实现一个根据筛选高交易额的交易，按照年份进行分组

- java8之前

> ```java
> Map<String, List<Transaction>> resultMap = new HashMap<>();
> for (Transaction transaction : transactions) {
>     if (transaction.getValue() > 1000) {
>         List<Transaction> tmpList = resultMap.get(transaction.getYear());
>         if (tmpList == null) {
>             tmpList = new ArrayList<>();
>         }
>         tmpList.add(transaction);
>     }
> }
> ```
>
> 称这种为指令式编程
>
> 1、这段代码一眼看过去并不能清晰的知道做了什么
>
> 2、每一步都是指令，告诉程序如何做

- java8的版本

> ```java
> Map<Integer, List<Transaction>> resultMap = transactions.stream()
>         .filter(transaction -> transaction.getValue() > 1000)
>         .collect(groupingBy(transaction -> transaction.getYear()));
> ```
>
> 称之为声明式编程
>
> 1、代码一眼看过去就知道这段代码是要做什么，从代码中能一下读出这段代码的逻辑。筛选交易额大于1000的交易，然后根据年份进行分组
>
> 2、没有指令操作，相较于java8之前，这个更像是告诉程序如何做，而不是怎么做

- 集合与流的差异

> 1、处理数据的方式不同
>
> - 集合需要自己去做迭代，循环去一个个处理数据
> - 流不需要自己做迭代，而只需要描述如何处理数据，循环已经内部化被封装到用户不可见。
>
> 这里衍生两个概念：
>
> 像这两种处理方式的不同，有2个专有名词，一个叫外部迭代，一个叫内部迭代。集合对数据的处理方式就是外部迭代，流对数据处理的方式是内部迭代。
>
> 2、是否可以并行处理数据
>
> - 集合无法对数据进行并行处理
> - 流可以利用多核CPU对数据进行并行处理
>
> 3、应用场景不同
>
> - 集合更偏向于对数据的存储
> - 流更偏向于对数据的计算
>
> 之所以这么说，参考Hadoop对数据的处理方式。举个例子，一个百亿数据的累加，在大数据领域的做法是把这个数据进行切分，分多个mapTask任务做部分计算结果。从这种方式看，效率是根据所分任务的数据提高的。所以，流可以使用多核CPU就意味着在大数据量的情况下，这种方式是比集合要好的。

### 行为参数化

- 概念

> 通过API来传递代码的能力。简单点来说，就是方法A可以作为参数传递给方法B，方法B的行为会根据方法A发生变化。

- 好处

> java8之前，一般是通过多个类实现同一接口的方法来达到不同行为。把不同的对象传入到一个方法对外方法中实现不同的行为。这种方式，一个是有许多不必要的模版代码，一个是每增加一种行为，都需要去实现一个子类。通过API传递代码意味着省去了子类的实现步骤，直接通过代码的方式即可控制不同的行为。

- 示例代码---实现过滤苹果的一个简单功能

> ```java
> /**
>  * 第一版：java8之前过滤红色的苹果
>  * 如果需求需要增加过滤绿色的苹果，这个时候需要复制黏贴一个相同的方法
>  * @param apples
>  * @return
>  */
> public static List<Apple> filterRedApple(List<Apple> apples) {
>     List<Apple> redApples = new ArrayList<>();
>     for (Apple apple : apples) {
>         if(apple.getColor().equals("red")){
>             redApples.add(apple);
>         }
>     }
>     return redApples;
> }
> 
> /**
>  * 第二版：过滤红色的苹果
>  * 通过把可变的业务逻辑进行抽离，达到了一定的灵活度
>  * 但是如果需求需要过滤苹果的重量或者同时过滤多个条件，则该方式不能满足
>  * @param apples
>  * @param color
>  * @return
>  */
> public static List<Apple> filterColorApple(List<Apple> apples, String color) {
>     List<Apple> colorApples = new ArrayList<>();
>     for (Apple apple : apples) {
>         if(apple.getColor().equals(color)){
>             colorApples.add(apple);
>         }
>     }
>     return colorApples;
> }
> ```
>
> 到目前为止，对过滤苹果的方法实现了一定的抽象，但是依然无法满足需求，如果用户需要根据重量进行过滤，以及后续更多的条件如何处理呢？这个时候就是需要把代码传递给过滤苹果的方法。

> ```java
> /**
>  * 第三版：这个实现使用到了几个概念，一个叫行为参数化(把行为作为参数传入方法决定方法的行为)，
>  * 一个叫函数式编程(把方法作为参数的编程思想)，
>  * 把过滤苹果的行为作为参数传入给该方法，能达到应对多变的需求,使用该方法的方式：
>  * 1、可以通过直接传入每个行为的实现类对象，缺点每一个行为都需要去实现子类，即使可能只会用到一次
>  * 2、使用匿名类，缺点空间占用大，模版代码多
>  * 3、使用lambda表达式，java8提供的方式，简洁，可读性强
>  * @param apples
>  * @param predicate
>  * @return
>  */
> public static List<Apple> filterApple(List<Apple> apples, Predicate<Apple> predicate) {
>     List<Apple> results = new ArrayList<>();
>     for (Apple apple : apples) {
>         if(predicate.test(apple)){
>             results.add(apple);
>         }
>     }
>     return results;
> }
> ```
>
> predicate是一个接口，该接口有一个test方法。那么使用的方式：
>
> 1、实现该接口传递不同行为的对象
>
> 2、使用匿名类
>
> 3、使用java8提供的lambda表达式

> ```java
> public static void main(String[] args) {
>     List<Apple> apples = Arrays.asList(
>             new Apple("red"),
>             new Apple("green"),
>             new Apple("yellow"));
>     // 通过匿名类的方式
>     List<Apple> redApples = filterApple(apples, new Predicate<Apple>() {
>         @Override
>         public boolean test(Apple apple) {
>             return apple.getColor().equals("red");
>         }
>     });
> 
>     // 通过lambda表达式的方式，对比匿名类的方式，代码简洁太多了
>     redApples = filterApple(apples, (Apple apple) -> apple.getColor().equals("red"));
> 
>     // 继续简化代码，参考的是lambda的类型推断
>     redApples = filterApple(apples, apple -> apple.getColor().equals("red"));
> }
> ```
>
> 通过把方法作为参数传递给苹果过滤方法，决定了苹果是根据颜色过滤还是根据重量等其他条件过滤。这就是行为参数化。	

### 并行与共享的可变数据

> 上面提到了java8可以并行的处理流，为了可以使用并行的方式，有个前提就是传递给流的方法需要是无状态的。也就是使不会修改共享的变量。这样的函数，一般称之为纯函数或者无副作用函数、无状态函数。