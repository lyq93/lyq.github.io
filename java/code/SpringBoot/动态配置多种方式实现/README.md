## 动态配置

### 获取配置的方式

- @Value

  ```java
  //通过注解的方式获取对象的值
  @Value("${person.id}")
  private Long id;
  ```

- 通过将配置与实体类绑定的方式

  > 后续代码呈现

### 实现动态配置

- 通过监听器的方式

  - 监听类

  ```java
  package com.sz.lyq.configurationDemo.context;
  
  import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
  import org.springframework.context.ApplicationListener;
  import org.springframework.context.EnvironmentAware;
  import org.springframework.core.Ordered;
  import org.springframework.core.env.ConfigurableEnvironment;
  import org.springframework.core.env.MapPropertySource;
  import org.springframework.core.env.MutablePropertySources;
  import org.springframework.core.env.PropertySource;
  
  import java.util.HashMap;
  import java.util.Map;
  
  public class CustomizedSpringBootApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {
      @Override
      public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
          ConfigurableEnvironment env = applicationEnvironmentPreparedEvent.getEnvironment();
          MutablePropertySources sources = env.getPropertySources();
          Map<String,Object> sourceMap = new HashMap<>();
          sourceMap.put("server.port","8081");
          PropertySource propertySource = new MapPropertySource("from_java_env",sourceMap);
          sources.addFirst(propertySource);
      }
  
      @Override
      public int getOrder() {
          return Ordered.HIGHEST_PRECEDENCE;
      }
  }
  ```

  > ApplicationEnvironmentPreparedEvent是环境配置准备好之前的一个事件，监听这个事件就可以修改配置
  >
  > Order接口是优先级接口，实现Order接口会有一个getOrder方法可以自定义优先级
  >
  > 而后需要把这个类配到spring.factories中让该类生效

```properties
# spring boot Application Listeners
org.springframework.context.ApplicationListener=\
com.sz.lyq.configurationDemo.context.CustomizedSpringBootApplicationListener
```

- 通过启动参数的方式

  > 通过运行程序时的启动参数也可以动态的修改配置，例如
  >
  >  --server.port=9090的方式可以修改端口号