[toc]



## SpringCloud Ribbon

### 增加Ribbon依赖

```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

### 配置Ribbon需要请求的服务

```java
package com.sz.lyq.springcloudribbonclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RibbonClients({
      @RibbonClient(name = "spring-cloud-service-provide")
})
@EnableDiscoveryClient
public class SpringCloudRibbonClientApplication {

   public static void main(String[] args) {
      SpringApplication.run(SpringCloudRibbonClientApplication.class, args);
   }

   @LoadBalanced
   @Bean
   public RestTemplate restTemplate() {
      return new RestTemplate();
   }
}
```

> @RibbonClients({
>       @RibbonClient(name = "spring-cloud-service-provide")
> })
>
> 需要请求的服务为 spring-cloud-service-provide
>
> 暴露RestTemplate，通过RestTemplate进行调用
>
> @LoadBalanced是客户端的复杂均衡

### 增加配置信息

```properties
## 应用名称
spring.application.name = spring-cloud-ribbon-client
## 端口
server.port = 8080
        
remote.host = localhost
remote.port = 7070
service.provide.name = spring-cloud-service-provide

### 配置ribbon 服务地提供方，这种是通过白名单的方式进行直连
spring-cloud-service-provide.ribbon.listOfServers = \
  http://${remote.host}:${remote.port}
```

### 编写调用Controller

```java
package com.sz.lyq.springcloudribbonclient.ribbonController;

import com.sz.lyq.springcloudribbonclient.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RibbonController {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${remote.host}")
    private String remoteHost;
    @Value("${remote.port}")
    private Integer remotePort;
    @Value("${service.provide.name}")
    private String serviceProvideName;

    @GetMapping("")
    public String user() {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        //通过直连的方式访问服务提供方
        return restTemplate.postForObject("http://" + serviceProvideName + "/user", user, String.class);
    }
}
```

> 1、service.provide.name即是配置文件里面的service.provide.name = spring-cloud-service-provide
>
> 2、由spring-cloud-service-provide.ribbon.listOfServers = \
>   http://${remote.host}:${remote.port} 知道服务器的信息
>
> 3、@Value("${service.provide.name}") 这是一种还算优雅的获取配置信息的方式

### 与eureka的整合

#### 增加eureka依赖

```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

#### 启用服务发现

```java
package com.sz.lyq.springcloudribbonclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RibbonClients({
      @RibbonClient(name = "spring-cloud-service-provide")
})
@EnableDiscoveryClient
public class SpringCloudRibbonClientApplication {

   public static void main(String[] args) {
      SpringApplication.run(SpringCloudRibbonClientApplication.class, args);
   }

   @LoadBalanced
   @Bean
   public RestTemplate restTemplate() {
      return new RestTemplate();
   }
}
```

> @EnableDiscoveryClient开启服务发现
>
> 这里有个需要注意的点：
>
> 如果使用eureka可以使用@EnableEurekaClient，但是没有使用。为什么？
>
> 在设计模式里面有一个观点是不要针对具体实现编程。服务发现不一定就只有eureka或者说不一定就只使用eureka。Spring用@EnableDiscoveryClient做服务发现就是不针对具体实现，如果要换成其他的服务发现客户端也是可以的。

#### 增加配置信息

```properties
### 向注册中心注册
eureka.client.register-with-eureka = true
### 向注册中心获取注册信息（服务、实例信息），通过服务发现的方式
eureka.client.fetch-registry = true
eureka.client.serviceUrl.defaultZone = http://localhost:9090/eureka
```

> 这样一来就会在eureka找寻对应的服务，然后进行服务调用。