## 配置服务器与eureka的整合

### 创建eureka服务器

#### 增加依赖

```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

#### 开启eureka服务

```java
package com.sz.lyq.springcloudeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SpringCloudEurekaServerApplication {

   public static void main(String[] args) {
      SpringApplication.run(SpringCloudEurekaServerApplication.class, args);
   }

}
```

#### 增加配置

```properties
## 配置应用名称
spring.application.name = spring-cloud-eureka-server
## 端口
server.port = 9090
## Spring Cloud Eureka 服务器作为注册中心
## 通常情况下，不需要再注册到其他注册中心去
## 同时，它也不需要获取客户端信息
### 取消向注册中心注册
eureka.client.register-with-eureka = false
### 取消向注册中心获取注册信息（服务、实例信息）
eureka.client.fetch-registry = false
## eureka地址
eureka.instance.hostname = localhost
eureka.client.serviceUrl.defaultZone = http://${eureka.instance.hostname}:${server.port}/eureka
```

### 创建配置服务器

#### 增加依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

#### 开启配置服务器和服务发现

```java
package spring.cloud.config.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

#### 增加配置信息

```properties
## 配置服务的应用名
spring.application.name = config-server
## 端口号
server.port = 7070

#配置服务器文件系统的git仓库
spring.cloud.config.server.git.uri = https://github.com/lyq93/tmp.git
spring.cloud.config.server.git.username = ****
spring.cloud.config.server.git.password = ****
##强制拉取git内容
spring.cloud.config.server.git.force-pull = true

## 向注册中心注册
eureka.client.registerWithEureka = true
## 拉取注册中心信息
eureka.client.fetchRegistry = true
## eureka注册中心地址
eureka.client.serviceUrl.defaultZone = http://localhost:10000/eureka
```

