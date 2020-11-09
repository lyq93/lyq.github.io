## Optional

### null带来的问题

> - 在开发过程中，会不断的去校验对象是否为null，导致代码深度嵌套，可读性差
> - null自身没有任何语义
> - 从java哲学层面上来说，java本身是避免让程序员意识到有指针的存在的，而空指针是一个例外
> - 不属于任务类型，逃脱了java的类型系统

### 使用Optional的好处

#### 使用Optional封装数据模型

```java
public class Person {

    private Car car;

    public Optional<Car> getCar() {
        return Optional.ofNullable(this.car);
    }

}

public class Car {

    private Insurance insurance;

    public Optional<Insurance> getInsurance() {
        return Optional.ofNullable(insurance);
    }

}

public class Insurance {

    private String name;

    public String getName() {
        return this.name;
    }

}
```

> 使用Optional封装数据模型的好处：
>
> - 语义丰富，例如用户调用getCar方法的时，通过返回值Optional<Car>就能知道该返回值有可能为空；而通过调用保险公司getName方法的时候，返回值没有用Optional封装，用户就知道该返回值不存在空值问题。就此来说，代码就非常一目了然。
> - 对于变量值的缺少导致的异常定位可以非常明确。例如在代码实现时，每个保险公司都必须是有名称的，所以如果通过getName方法出现了空指针问题，那么一定就是数据上的问题

#### Optional方法介绍

- empty

> 返回一个空的Optional实例

- filter

> 如果值存在并且满足提供的条件，则返回包含该值的Optional对象，否则返回一个空Optional对象。
>
> ```java
> Optional<Insurance> optionalInsurance = Optional.ofNullable(new Insurance());
> optionalInsurance
>         .filter(insurance -> "test".equals(insurance.getName()))
>         .ifPresent(x -> System.out.println(x));
> ```

- flatMap

> 如果值存在，则对该值执行mapping函数调用，返回一个Optional类型的值；否则返回一个空的Optional对象

- get

> 如果值存在，就将被Optional封装的值返回；否则抛出一个NoSuchElementException

- ifPresent

> 如果值存在，则执行使用该值的方法；否则什么都不做

- ispresent

> 如果值存在，返回true；否则返回false

- map

> 如果值存在，调用mapping函数

- of

> 将指定的值用Optional封装返回；如果值为null，则抛出一个空指针异常

- ofNullable

> 将指定的值用Optional封装返回；如果该值为null，返回一个空的Optional对象

- ofElse

> 如果有值则返回该值，否则返回一个默认值

- ofElseGet

> 如果有值则返回该值，否则返回一个指定的supplier接口生成的值

- orElseThrow

> 如果有值则返回该值，否则返回一个指定的supplier接口生成的异常

#### Optional的实战示例

- 用Optional封装可能为null的值

> ```java
> Optional<String> test = Optional.ofNullable(map.get("test"));
> ```

- 异常与Optional的对比

> 一个方法，可能存在对某一种条件（值）无法给出返回值，这种时候有可能的处理情况就是抛出一个异常或者返回null。
>
> 例如Integer.parseInt(String)方法，如果String不为整数的情况下，会直接抛出一个异常。如果通过使用Optional来对这种异常进行处理的话，可以是这样的
>
> ```java
> public Optional<Integer> String2Int(String s) {
>     try {
>         return Optional.of(Integer.parseInt(s));
>     }catch (Exception e) {
>         return Optional.empty();
>     }
> }
> ```

#### 总结

> 可以把Optional看作是只存一个数据的Stream。另外尽可能的多使用Optional对象，它能带来的好处或者说它可以避免null值这种存在导致的诸多问题。
>
> Optional能带来的好处包括：
>
> - 丰富了语义，使得开发者见到Optional就知道可能存在空值的情况
> - 可以使开发者更积极的去解引用Optional对象
> - Optional还支持类似于流的map，filter，flatMap方法