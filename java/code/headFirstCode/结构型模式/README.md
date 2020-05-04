## 代理模式

### 动态代理

#### 角色

- 被代理对象接口
- 被代理对象实现
- InvocationHandler实现
- 测试类

#### 代码

- 被代理对象接口

  ```java
  package com.sz.lyq.proxyDemo.demoClass;
  
  public interface PersonOperation {
  
      /**
       * 获取名字
       */
      void getName();
  
      /**
       * 获取身份号
       */
      void getIdentify();
  }
  ```

- 代理对象实现

  ```java
  package com.sz.lyq.proxyDemo.demoClass;
  
  /**
   * 被代理对象，实现接口
   * 动态代理基于接口实现的
   */
  public class PersonHandler implements PersonOperation {
  
      @Override
      public void getName() {
          System.out.println("person name");
      }
  
      @Override
      public void getIdentify() {
          System.out.println("person identify");
      }
  }
  ```

- InvocationHandler实现

  ```java
  package com.sz.lyq.proxyDemo.demoClass;
  
  import java.lang.reflect.InvocationHandler;
  import java.lang.reflect.Method;
  
  /**
   * 实现java API InvocationHandler
   * 代理对象的每一次想法调用都会到invoke方法里面来
   */
  public class MyInvocationHandler implements InvocationHandler {
      // 被代理对象
      private PersonHandler personHandler;
      // 构造器注入
      public MyInvocationHandler(PersonHandler personHandler) {
          this.personHandler = personHandler;
      }
  
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          return method.invoke(personHandler,args);
      }
  }
  ```

- 测试类

  ```java
  package com.sz.lyq.proxyDemo.demoClass;
  
  import java.lang.reflect.Proxy;
  
  public class ProxyMain {
      public static void main(String[] args) {
          // 产生代理对象
          PersonOperation proxyInstance = (PersonOperation)Proxy.newProxyInstance(
                  PersonHandler.class.getClassLoader(),
                  PersonHandler.class.getInterfaces(), // 动态代理是基于接口的
                  new MyInvocationHandler(new PersonHandler()));
  
          // 方法调用，进入到实现invocationHandler接口的invoke方法
          proxyInstance.getName();
      }
  
  }
  ```

  > Proxy.newProxyInstance需要传入被代理类的接口类，这就是动态代理为什么是基于接口实现

