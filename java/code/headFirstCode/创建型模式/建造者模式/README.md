## 建造者模式

### 背景

> 假设造一栋房子，造房子的流程为打桩，砌墙，封顶。房子有各种各样的，例如普通房，高楼，别墅等。各种房子的建造过程是一样的，但是要求不同。
>
> 如何实现

### 传统实现

> 一个接口类，定义打桩，砌墙，封顶，以及造房四个方法。
>
> 各种房子分别实现对应的方法，client要造什么房子分别使用对应的房子实现类去实现

- 造房接口类

> ```java
> public interface IBuildHouse {
> 
>     void buildBasic();
> 
>     void buildWalls();
> 
>     void buildRoofed();
> 
>     void build();
> }
> ```

- 子类实现类

> ```java
> public class CommonHouse implements  IBuildHouse {
>     @Override
>     public void buildBasic() {
>         System.out.println("common house build basic");
>     }
> 
>     @Override
>     public void buildWalls() {
>         System.out.println("common house build walls");
>     }
> 
>     @Override
>     public void buildRoofed() {
>         System.out.println("common house build roofed");
>     }
> 
>     @Override
>     public void build() {
>         buildBasic();
>         buildWalls();
>         buildRoofed();
>     }
> }
> ```

- 客户端类

> ```java
> public class Client {
>     public static void main(String[] args) {
>         CommonHouse commonHouse = new CommonHouse();
>         commonHouse.build();
>     }
> }
> ```

#### 存在的问题

> 这种实现虽然简单，但是没有设计感
>
> 房子是产品，造房子是一系列流程最后产出一个产品。那么这里就可以分为两个部分，一个是产品，一个是流程。传统方式下这两部分是耦合在一起的
>
> 这就导致扩展性和维护性不好

### 建造者模式

#### 背景

> 简单来说，该模式的出现就是为了解决相应的问题。这个模式把构造复杂对象的建造过程抽离出来，一步一步的创建出一个复杂的对象
>
> 建造者模式有四个角色：
>
> 1、产品
>
> 一个具体的产品对象，如上述例子中，就是房子
>
> 2、建造者
>
> 创建一个产品的各个部分的一个接口或者抽象类
>
> 3、具体的建造者
>
> 实现接口，构建和装配各个部分
>
> 4、指挥者
>
> 构建一个是用builder接口的对象。主要就是隔离客户与对象的生产过程，负责控制对象的生产过程

#### 实现

- 产品

> ```java
> @Data
> public class House {
> 
>     private String basic;
>     private String wall;
>     private String roofed;
> 
> }
> ```

- 建造者

> ```java
> public interface IBuildHouse {
> 
>     House house = new House();
> 
>     void buildBasic();
> 
>     void buildWalls();
> 
>     void buildRoofed();
> 
>     default House build() {
>         return house;
>     };
> }
> ```

- 具体建造者

> ```java
> public class CommonHouse implements  IBuildHouse {
>     @Override
>     public void buildBasic() {
>         System.out.println("common house build basic");
>     }
> 
>     @Override
>     public void buildWalls() {
>         System.out.println("common house build walls");
>     }
> 
>     @Override
>     public void buildRoofed() {
>         System.out.println("common house build roofed");
>     }
> }
> ```

- 指挥者

> ```java
> public class HouseDirector {
> 
>     IBuildHouse buildHouse = null;
> 
>     public HouseDirector(IBuildHouse buildHouse) {
>         this.buildHouse = buildHouse;
>     }
> 
>     public void setBuildHouse(IBuildHouse buildHouse) {
>         this.buildHouse = buildHouse;
>     }
> 
>     public House constructHouse() {
>         buildHouse.buildBasic();
>         buildHouse.buildWalls();
>         buildHouse.buildRoofed();
>         return buildHouse.build();
>     }
> 
> }
> ```

> 建造者模式
>
> 把产品和产品的创建过程解耦，使得相同的创建过程可以创建不同的产品
>
> 封装了产品的内部组成细节，客户端无需关心产品是如何产生的
>
> 每个具体建造者之间都是相互独立的，因此可以方便的新加或者减少具体建造者，用户使用不同的具体建造者即可得到不同的产品

#### 应用

> 查阅java.lang.StringBuilder类