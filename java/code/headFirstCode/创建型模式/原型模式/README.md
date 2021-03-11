## 原型模式

### 背景

> 考虑克隆羊问题，如果有一只羊，名字是tony，年龄是1，颜色是白色。如果要复制10个跟它一样的羊，如何编写程序？
>
> 原型模式是一种创建型模式，就是用来专门处理这种问题
>
> 原型模式的优点：
>
> 1、创建对象使用的是Object类的clone方法，这个方法是一个本地方法，它直接操作内存的二进制流，所以比起new对象而言，性能上是有提升，特别是大对象的情况下
>
> 2、简化对象的创建，如果不使用原型模式，创建相同的对象要一只重复给相同的属性值，代码笨重且语义不清

### 实现

- 说明

> 1、原型类
>
> 主要是两点：
>
> 实现Cloneable接口
>
> 查阅Cloneable接口发现该接口是个空接口，一般这种接口就是类似于一种标记接口。比如ArrayList里面的随机访问接口。Cloneable接口表明或者说告诉虚拟机实现了该接口的类，可以进行clone操作
>
> 重载clone方法
>
> 在Java中，所有类的父类都是Object类。Object类有一个clone方法，返回对象的一个拷贝

> 2、获取复制对象的调用方

- 代码

> ```java
> @Data
> public class Sheep implements Cloneable {
> 
>     private String name;
>     private String age;
>     private String color;
>     public Sheep friend;
> 
>     public Sheep(String name, String age, String color) {
>         super();
>         this.name = name;
>         this.age = age;
>         this.color = color;
>     }
> 
>     @Override
>     public Object clone() {
>         Sheep sheep = null;
>         try {
>             sheep = (Sheep) super.clone();
>         }catch (Exception e) {
>             System.out.println("error");
>         }
>         return sheep;
>     }
> }
> 
> public class Client {
>     public static void main(String[] args) {
>         Sheep sheep = new Sheep("nancy","17","red");
>         sheep.friend = new Sheep("jack", "22", "green");
> 
>         Sheep sheep1 = (Sheep)sheep.clone();
>         Sheep sheep2 = (Sheep)sheep.clone();
>         Sheep sheep3 = (Sheep)sheep.clone();
>         Sheep sheep4 = (Sheep)sheep.clone();
> 
>         System.out.println("sheep=" + sheep + "sheep.friend=" +sheep.friend.hashCode());
>         System.out.println("sheep1=" + sheep1 + "sheep1.friend=" +sheep1.friend.hashCode());
>         System.out.println("sheep2=" + sheep2 + "sheep2.friend=" +sheep2.friend.hashCode());
>         System.out.println("sheep3=" + sheep3 + "sheep3.friend=" +sheep3.friend.hashCode());
>         System.out.println("sheep4=" + sheep4 + "sheep4.friend=" +sheep4.friend.hashCode());
>     }
> }
> ```

- 应用

> 在Spring中，原型bean的创建就是用的原型模式。
>
> Beans.xml:
>
> <bean id="1" class="xxx" scope="prototype"/>
>
> getBean() -> doGetBean() -> mdb.isPrototype()

- 深/浅拷贝介绍

> 浅拷贝：
>
> 对于基本数据类型而言，浅拷贝就是把属性值赋值给新对象
>
> 对于引用数据类型而言，浅拷贝就是把引用（内存地址）赋值给新对象。所以两者指向同一个实例，如果原实例修改，现有对象也会发生改变
>
> 深拷贝：
>
> 复制对象的所有基本数据类型的成员变量值
>
> 为所有引用数据类型的成员变量申请存储空间，并复制每个引用数据类型成员变量所引用的对象，直到该对象可达的所有对象。也就是说，对整个对象进行拷贝

