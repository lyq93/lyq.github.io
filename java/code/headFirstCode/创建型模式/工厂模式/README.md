[toc]

# 工厂模式

## 简单工厂

### 定义

​	简单工厂不算是一种模式，只能说是一种编程习惯。

	### 相关类
	
	- ToyFactory.java (工厂类，生产玩具)
	- AnimalToy.java (动物玩具接口)
	- DuckToy.java (具体玩具实现类)
	- TigerToy.java (具体玩具实现类)

### 相关实现

 - ToyFactory.java

   ```java
   package com.sz.lyq.factoryDemo.simpleFactory;
   
   /**
    * 生产玩具的工厂
    */
   public class ToyFactory {
   
       /**
        *第一版工厂方法
        * 存在问题：
        * 1、每增加一种类型的玩具都需要修改工厂方法
        * 2、存在硬编码
        * 考虑：
        * 是否应该把所有种类的玩具都放在这个工厂方法里面来创建
        * 如果在不同城市生产的玩具外形是不一样的，那怎么办
        * @param toyName
        * @return
        */
       public static AnimalToy createToy(String toyName){
           AnimalToy animalToy = null;
           if("duck".equals(toyName)) {
               animalToy = new DuckToy();
           } else if("tiger".equals(toyName)) {
               animalToy = new TigerToy();
           }
   
           return animalToy;
       }
   
       /**
        * 第二版
        * 通过反射的方式解决第一版存在的问题
        * 异常的处理方式不能直接外抛让调用方处理，而是方法内部消化
        * @param clazz
        * @return
        * @throws Exception
        */
       public static Object createToyByReflect(Class<? extends AnimalToy> clazz) {
   
           Object obj = null;
           try {
               obj = Class.forName(clazz.getName()).newInstance();
           }catch (Exception e) {
               e.printStackTrace();
           }
   
           return obj;
       }
   
   }
   ```

   > 该类中有两个版本的实现，第一个版本的实现是针对了具体类进行编程，违反了对修改关闭的原则，每增加一个实现类都需要重新修改工厂类。第二个版本通过反射的方式优化了相关问题

 - AnimalToy.java

   ```java
   package com.sz.lyq.factoryDemo.simpleFactory;
   
   /**
    * 动物玩具接口
    */
   public interface AnimalToy {
       /**
        * 玩具外形接口
        */
       void shape();
   
   }
   ```

   > 动物玩具接口，有个打印动物外形的方法

 - DuckToy.java

   ```java
   package com.sz.lyq.factoryDemo.simpleFactory;
   
   public class DuckToy implements AnimalToy {
   
       @Override
       public void shape() {
           System.out.println("this is duck toy");
       }
   }
   ```

 - TigerToy.java

   ```java
   package com.sz.lyq.factoryDemo.simpleFactory;
   
   public class TigerToy implements AnimalToy {
       @Override
       public void shape() {
           System.out.println("this is tiger toy");
       }
   }
   ```

### 测试

```java
package com.sz.lyq.factoryDemo.simpleFactory;

public class SimpleFactoryMain {

    public static void main(String[] args) {
        // 第一版工厂测试
        DuckToy duck = (DuckToy)ToyFactory.createToy("duck");
        duck.shape();

        // 第二版工厂测试
        DuckToy duckToy = (DuckToy)ToyFactory.createToyByReflect(DuckToy.class);
        duckToy.shape();
    }
}
```

### 思考

> 简单工厂是实现了生产玩具的功能，但是如果不同区域的生产的鸭子是不一样的，又需要如何处理？

## 工厂方法模式

### 定义

​	定义了一个创建对象的接口，但由子类决定要实例化的类是哪一个。工厂方法让类把实例化推迟到子类。

### 相关类

- 抽象工厂
- 具体工厂
- 产品接口
- 产品实现

### 相关实现

- 抽象工厂

  ```java
  package com.sz.lyq.factoryDemo.factoryMethod;
  
  public abstract class ToyFactory {
  
      public abstract Object createToy(String cityName);
  
  }
  ```

- 具体工厂

  ```java
  package com.sz.lyq.factoryDemo.factoryMethod;
  
  /**
   * 生产鸭子玩具的工厂
   */
  public class DuckFactory extends ToyFactory {
      @Override
      public AnimalToy createToy(String cityName) {
          AnimalToy animalToy = null;
          if("shanghai".equals(cityName)) {
              animalToy = new DuckBySHToy();
          } else if("shenzhen".equals(cityName)) {
              animalToy = new DuckBySZToy();
          }
  
          return animalToy;
      }
  }
  ```

  > 工厂方法根据区域名生产各自区域的动物玩具

- 产品接口

  ```java
  package com.sz.lyq.factoryDemo.factoryMethod;
  
  /**
   * 动物玩具基类
   */
  public abstract class AnimalToy {
  
      public abstract void shape();
  
  }
  ```

- 产品实现

  ```java
  package com.sz.lyq.factoryDemo.factoryMethod;
  
  public class DuckBySHToy extends AnimalToy {
  
      @Override
      public void shape() {
          System.out.println("上海生产的鸭子玩具外形");
      }
  }
  ```

  ```java
  package com.sz.lyq.factoryDemo.factoryMethod;
  
  public class DuckBySZToy extends AnimalToy {
      @Override
      public void shape() {
          System.out.println("深圳生产的鸭子玩具外形");
      }
  }
  ```

### 测试

```java
package com.sz.lyq.factoryDemo.factoryMethod;

public class FactoryMethodMain {

    public static void main(String[] args) {
        ToyFactory toyFactory = new DuckFactory();
        DuckBySHToy duckBySHToy = (DuckBySHToy)toyFactory.createToy("shanghai");
        duckBySHToy.shape();
    }
}
```

### 思考

> 工厂方法实现了不同区域生产不同的动物玩具，如果说这时候需要生产商生产动物玩具的一系列依赖产品，又该如何处理？

### 应用

> JDK的Calendar类

