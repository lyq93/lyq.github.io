## SpringCloud Zuul

### 创建Zuul服务

#### 增加依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```

#### 启动Zuul及服务发现

```java
package spring.cloud.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class ZuulProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulProxyApplication.class, args);
    }

}
```

#### 增加相应配置

##### bootstrap.properties

```properties
## 应用名
spring.application.name = zuul-proxy
## 通过服务注册与发现的方式来实现
## 取消向注册中心注册
eureka.client.registerWithEureka = true
## 取消拉取注册中心信息
eureka.client.fetchRegistry = true
## eureka注册中心地址
eureka.client.serviceUrl.defaultZone = http://localhost:10000/eureka

##该项为配置服务器的默认配置文件的文件名
spring.cloud.config.name = zuulConfig
##该项为取哪个环境的配置
spring.cloud.config.profile = default
##label
spring.cloud.config.label = master
## 激活 Config Server 服务发现
spring.cloud.config.discovery.enabled = true
## Config Server 服务器应用名称
spring.cloud.config.discovery.serviceId = config-server
```

> 1、eureka的配置信息
>
> 2、配置服务器的配置信息

#### 配置服务器上的zuul.properties

![image-20200508145230171](/Users/liuyongqian/Library/Application Support/typora-user-images/image-20200508145230171.png)

