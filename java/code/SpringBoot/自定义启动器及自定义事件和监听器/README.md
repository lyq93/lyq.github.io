## 自定义启动器

### 创建配置类

```java
package com.sz.lyq.starterDemo.autoconfig;

import com.sz.lyq.starterDemo.domian.Person;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "person",name = "enabled", havingValue = "true", matchIfMissing = true)
public class PersonAutoConfiguration {
    @ConfigurationProperties(value = "person")
    @Bean
    public Person person(){
        return new Person();
    }
}
```

> @Configuration 标识该类为一个配置类
>
> @ConditionalOnWebApplication 表示必须在Web应用中存在
>
> @ConditionalOnProperty 匹配person.enabled配置，默认值为true，不存在该配置也为true

### 创建跟配置挂钩的实体类

```java
package com.sz.lyq.starterDemo.domian;

import org.springframework.boot.context.properties.ConfigurationProperties;

public class Person {
    private Long id;
    private String name;
    private Integer age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
```

### 配置相应的配置

- application.properties

```properties
person.enabled=true

person.id=1
person.name=lyq
person.age=27
```

## 自定义事件和监听

- 自定义事件

  ```java
  public static class MyApplicationEvent extends ApplicationEvent {
  
          /**
           * Create a new {@code ApplicationEvent}.
           *
           * @param source the object on which the event initially occurred or with
           *               which the event is associated (never {@code null})
           */
          public MyApplicationEvent(Object source) {
              super(source);
          }
      }
  ```

- 自定义监听

  ```java
  public static class MyApplicationListener implements ApplicationListener<MyApplicationEvent> {
  
      @Override
      public void onApplicationEvent(MyApplicationEvent event) {
          System.out.println("MyApplicationListener receives event resource:" + event.getSource());
      }
  }
  ```

- 发布事件

  ```java
  public static void main(String[] args) {
          /**
           * 该注解上下文通过查看ApplicationContext实现可得知
           * 并且通过查看源码的方式可知有两种注册监听的方式（1.构造器直接传入component类型，类似于Application run
           * 2.通过API的方式，即register方法）
           */
          //通过构造器的方式注册listener，这种方式在构造器内部已经调用了refresh方法，不需要额外再次通过API方式调用，否则会报异常
          AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyApplicationListener.class);
  
          //通过API的方式注册listener
  //        context.register(MyApplicationListener.class);
  
  //        context.refresh();
  
          context.publishEvent(new MyApplicationEvent("hello world"));
  
      }
  ```

