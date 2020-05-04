[toc]

## 观察者模式

### 定义

> 定义对象间的一对多依赖，这样一来，当一个对象的状态发生改变的时候，它的依赖者都会收到通知并且自动更新。

### 角色

- 主题接口
- 主题实现
- 观察者接口
- 观察者实现
- 数据处理接口

### 代码

- 主题接口

  ```java
  package com.sz.lyq.observerDemo.demoClass;
  
  /**
   * 类似于java API 的 Observable类
   */
  public interface Subject {
      /**
       * 注册观察者
       * @param observer
       * @return
       */
      boolean registerObserver(Observer observer);
  
      /**
       * 移除观察者
       * @param observer
       * @return
       */
      boolean removeObserver(Observer observer);
  
      /**
       * 通知观察者
       * @return
       */
      boolean notifyObserver();
  }
  ```

  > registerObserver注册一个观察者来获取主题变化通知
  >
  > removeObserver移除一个观察者
  >
  > notifyObserver通知所有观察者

- 主题实现

  ```java
  package com.sz.lyq.observerDemo.demoClass;
  
  import java.util.ArrayList;
  import java.util.List;
  
  /**
   * 主题类
   */
  public class SubjectImpl implements Subject {
  
      List<Observer> observers = null;
      // 模拟数据
      private Integer data_x;
      private Double data_y;
  
      public SubjectImpl() {
          observers = new ArrayList<>();
      }
  
      @Override
      public boolean registerObserver(Observer observer) {
          observers.add(observer);
          return true;
      }
  
      @Override
      public boolean removeObserver(Observer observer) {
          int i = observers.indexOf(observer);
          observers.remove(i);
          return true;
      }
  
      @Override
      public boolean notifyObserver() {
          // 通知所有观察者
          for(Observer observer : observers) {
              observer.update(data_x,data_y);
          }
          return true;
      }
  
      /**
       * 主题类数据变动方法
       */
      public void dataChange() {
          notifyObserver();
      }
  
      /**
       * 模拟数据发生改变
       * @param data_x
       * @param data_y
       */
      public void setData(Integer data_x,Double data_y) {
          this.data_x = data_x;
          this.data_y = data_y;
          dataChange();
      }
  }
  ```

  > 通过构造器初始化内部维护观察者的List，setData模拟数据改变，数据发生改变之后调用dataChange通知所有观察者

- 观察者接口

  ```java
  package com.sz.lyq.observerDemo.demoClass;
  
  /**
   * java API Observer接口
   */
  public interface Observer {
      void update(Integer data_x,Double data_y);
  }
  ```

  > 观察者实现Observer接口，利用update方法对改变的数据进行处理

- 观察者实现

  ```java
  package com.sz.lyq.observerDemo.demoClass;
  
  /**
   * 观察者类
   */
  public class ObserverImpl implements Observer,DisplayData {
      // 观察的数据
      private Integer data_x;
      private Double data_y;
      private Subject subject;
  
      public ObserverImpl(Subject subject) {
          this.subject = subject;
          subject.registerObserver(this);
      }
  
      @Override
      public void update(Integer data_x, Double data_y) {
          this.data_x = data_x;
          this.data_y = data_y;
          display();
      }
  
      @Override
      public void display() {
          System.out.println("data_x:" + data_x + ",data_y:" + data_y);
      }
  }
  ```

  > 通过组合的方式，把主题实现通过构造器注入进来并直接初始化注册当前观察者

- 数据处理接口

  ```java
  package com.sz.lyq.observerDemo.demoClass;
  
  /**
   * 数据展示接口
   */
  public interface DisplayData {
      void display();
  }
  ```

  > 观察者把改变的数据通过这个接口进行展示或者做其他用途

### 测试

```java
package com.sz.lyq.observerDemo.demoClass;

public class ObserverMain {

    public static void main(String[] args) {
        SubjectImpl subject = new SubjectImpl();

        // 通过构造器默认把类注册成为观察者
        ObserverImpl observer = new ObserverImpl(subject);

        // 模拟数据发生改动
        subject.setData(1,1.6);
    }
}
```

