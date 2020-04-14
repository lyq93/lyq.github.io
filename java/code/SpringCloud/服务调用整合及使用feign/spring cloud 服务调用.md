[toc]

# spring cloud 服务调用

## 用户API服务 user-api

### 用户接口

```java
package spring.cloud.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import spring.cloud.service.domain.User;
import spring.cloud.service.fallback.UserServiceFallBack;

import java.util.List;

@FeignClient(name = "${user.service.name}", fallback = UserServiceFallBack.class) //使用占位符的方式避免硬编码
public interface UserService {

    @PostMapping("/user/save")
    public Boolean saveUser(User user);

    @GetMapping("/user/list")
    public List<User> getUsers();

}
```

## 服务提供方 user-service-provider

### application.properties

```properties
## 应用名
spring.application.name = user-service-provider
## 开启eureka（默认是开启的）
eureka.client.enabled = true
## 应用端口
server.port = 9090

## 取消向注册中心注册
eureka.client.registerWithEureka = true
## 取消拉取注册中心信息
eureka.client.fetchRegistry = true
## eureka注册中心地址
eureka.client.serviceUrl.defaultZone = http://localhost:10000/eureka
```

### 用户API接口实现

```java
package spring.cloud.service.provider.service;

import org.springframework.stereotype.Service;
import spring.cloud.service.api.UserService;
import spring.cloud.service.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryUserService implements UserService {

    private Map<Long,User> userMap = new ConcurrentHashMap<Long,User>();

    @Override
    public Boolean saveUser(User user) {
        return userMap.put(user.getId(),user) == null;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList(userMap.values());
    }
}
```

### userController

```java
package spring.cloud.service.provider.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.cloud.service.api.UserService;
import spring.cloud.service.domain.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
     * 这是服务端做hystrix熔断处理，hystrix也可以在客户端做处理
     * @return
     * @throws Exception
     */
    @HystrixCommand(commandProperties = { //设置超时时长
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "100")
    },
    fallbackMethod = "fallbackMethodHandler")
    @GetMapping("/user/list")
    public List<User> getUsers() throws Exception {
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
    public List<User> fallbackMethodHandler() {
        return Collections.emptyList();
    }
}
```

## 用户客户端服务 user-service-client

### bootstrap.properties

```properties
## 应用名
spring.application.name = spring-cloud-service-client

## 通过服务注册与发现的方式来实现
## 取消向注册中心注册
eureka.client.registerWithEureka = true
## 取消拉取注册中心信息
eureka.client.fetchRegistry = true
## eureka注册中心地址
eureka.client.serviceUrl.defaultZone = http://localhost:10000/eureka

##该项为配置服务器的默认配置文件的文件名
spring.cloud.config.name = serverConfig
##该项为取哪个环境的配置
spring.cloud.config.profile = test
##label
spring.cloud.config.label = master
## 激活 Config Server 服务发现
spring.cloud.config.discovery.enabled = true
## Config Server 服务器应用名称
spring.cloud.config.discovery.serviceId = config-server
```

` 客户端通过eureka注册中心发现服务提供方和配置服务器`

### application.properties

```properties
## 应用端口
server.port = 8080

## 以下配置使用配置服务器来代替
## 服务提供方IP
##provider.service.host = localhost
## 服务提供方端口
##provider.service.port = 9090
## 服务提供方应用名称
##provider.service.name = user-service-provider
## userService接口的提供方
##user.service.name = ${provider.service.name}

## 通过直连的方式进行服务调用
## user-service-provider.ribbon.listOfServers = \
##  http://${provider.service.host}:${provider.service.port}
```

` 1、通过使用ribbon的方式可以直连访问服务提供方，但问题在于服务方的IP和端口是配死在客户端的，这导致服务提供方的IP和端口不能轻易发生变化导致强耦合。` 

`2、使用ribbon是自己手写客户端请求代码（1、使用restTemplate方式；2、loadBalanceClient方式），所以使用feign进行整合。`

` 3、部分配置不应该存在于用户客户端服务，修改起来会是个巨大工作量，所以整合配置服务器，通过服务发现的方式把客户端的配置移到配置服务器上`

##  注册中心服务eureka-server

### application.properties

```properties
## eureka服务应用名
spring.application.name = eureka-server
## 主机
eureka.instance.hostname = localhost
## 端口
server.port = 10000
## 取消向注册中心注册
eureka.client.registerWithEureka = false
## 取消拉取注册中心信息
eureka.client.fetchRegistry = false
## 注册中心地址
eureka.client.serviceUrl.defaultZone = http://${eureka.instance.hostname}:${server.port}/eureka
```

## 配置服务器 config-server

### application.properties

```properties
## 配置服务的应用名
spring.application.name = config-server
## 端口号
server.port = 7070

#配置服务器文件系统的git仓库
spring.cloud.config.server.git.uri = https://github.com/lyq93/tmp.git
spring.cloud.config.server.git.username = 407539854@qq.com
spring.cloud.config.server.git.password = --
##强制拉取git内容
spring.cloud.config.server.git.force-pull = true

## 向注册中心注册
eureka.client.registerWithEureka = true
## 拉取注册中心信息
eureka.client.fetchRegistry = true
## eureka注册中心地址
eureka.client.serviceUrl.defaultZone = http://localhost:10000/eureka
```

` 配置服务器的配置可分多个版本，例如serverConfig.properties为默认配置,serverConfig-test.properties为测试环境配置。用户客户端服务通过指定相应环境配置来获取对应的配置。例如spring.cloud.config.profile = test`