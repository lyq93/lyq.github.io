## 使用Hystrix

### 服务端Hystrix使用

#### 增加依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

#### 开启hystrix

```java
package spring.cloud.service.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableHystrix
public class UserServiceProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceProviderApplication.class, args);
    }

}
```

#### Controller实现

```java
package spring.cloud.service.provider.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.cloud.ribbon.api.UserService;
import spring.cloud.ribbon.domain.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

@RestController
public class UserController {

    private final Random random = new Random();

    @Autowired
    private UserService userService;

    @PostMapping("/user/save")
    public Boolean saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    /**
     * hystrix熔断处理
     * @return
     * @throws Exception
     */
    @HystrixCommand(commandProperties = { //设置超时时长
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "100")
    },
    fallbackMethod = "fallbackMethodHandler")
    @GetMapping("/user/list")
    public Collection<User> getUsers() throws Exception {
        //产生一个随机的时间
        long executeTime = random.nextInt(200);

        System.out.println("execute time:" + executeTime + " ms");

        Thread.sleep(executeTime);

        return userService.getUsers();
    }

    /**
     * 返回空集合
     * @return
     */
    public Collection<User> fallbackMethodHandler() {
        return Collections.emptyList();
    }
}
```

### 客户端hystrix使用

#### 增加依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

#### hystrix实现

```java
package spring.cloud.ribbon.client.web.controller;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import spring.cloud.ribbon.domain.User;

import java.util.Collection;
import java.util.Collections;

/**
 * 客户端实现hystrix
 */
public class UserRibbonControllerHystrixCommand extends HystrixCommand<Collection> {
    // 需要调用的服务名
    private String providerServiceName;
    
    private final RestTemplate restTemplate;

    /**
     * 通过构造器注入参数及设置熔断条件
     * @param providerServiceName
     * @param restTemplate
     */
    public UserRibbonControllerHystrixCommand(String providerServiceName, RestTemplate restTemplate) {
        super(HystrixCommandGroupKey.Factory.asKey("user-ribbon-client"),
                100);
        this.providerServiceName = providerServiceName;
        this.restTemplate = restTemplate;
    }

    /**
     * 主要执行逻辑
     * @return
     * @throws Exception
     */
    @Override
    protected Collection run() throws Exception {
        String url = "http://" + providerServiceName + "/user/list";
        return restTemplate.getForObject(url,Collection.class);
    }

    /**
     * 异常回调
     * @return
     */
    @Override
    protected Collection getFallback() {
        return Collections.emptyList();
    }
}
```

#### 测试

```java
/**
 * 客户端实现hystrix发起的请求
 * @return
 */
@GetMapping("/users2")
public Collection<User> getUsersAddHystrix() {
    return new UserRibbonControllerHystrixCommand(serviceProvider,restTemplate).execute();
}
```