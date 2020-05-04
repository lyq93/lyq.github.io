## 模版模式

### 定义

> 在一个方法中定义一个算法骨架，然后将一些步骤延迟到子类中，模版方法使得子类在不改变算法结构的前提下，重新定义算法的步骤

### 角色

- 抽象模版类
- 具体模版类
- 测试

### 代码

- 抽象模版类

  ```java
  package com.sz.lyq.templateDemo.demoClass;
  
  public abstract class AbstractTemplate {
  
      /**
       * 这是模版方法，加final不允许修改
       */
      public final void templateMethod() {
          methodA();
          //基类的默认hock方法实现为true
          if(hock()) {
              methodB();
          }
          methodC();
          methodD();
      }
  
      /**
       * 当前类实现，如果不允许修改的话，可以加final
       */
      public final void methodA() {
          System.out.println("this is methodA");
      }
  
      /**
       * 当前类实现，如果不允许修改的话，可以加final
       */
      public final void methodB() {
          System.out.println("this is methodB");
      }
  
      /**
       * 由子类实现的方法
       */
      public abstract void methodC();
  
      /**
       * 由子类实现的方法
       */
      public abstract void methodD();
  
      /**
       * 这是一个钩子方法，可以在模版方法中当条件进行逻辑判断
       * 增加模版方法的灵活度
       * @return
       */
      public boolean hock(){
          return true;
      }
  }
  ```

- 具体模版类

  ```java
  package com.sz.lyq.templateDemo.demoClass;
  
  public class TemplateClass extends AbstractTemplate {
      @Override
      public void methodC() {
          System.out.println("this is methodC");
      }
  
      @Override
      public void methodD() {
          System.out.println("this is methodD");
      }
  
      /**
       * 钩子方法，可选实现
       * @return
       */
      @Override
      public boolean hock() {
          return false;
      }
  }
  ```

  > 子类可以实现钩子方法决定模版方法里面的某些步骤是否执行

### 测试

```java
package com.sz.lyq.templateDemo.demoClass;

public class TemplateDemoMain {

    public static void main(String[] args) {
        TemplateClass templateClass = new TemplateClass();
        //通过子类调用父类的模版方法，执行定义好的算法
        templateClass.templateMethod();
    }

}
```

