[toc]

## SpringCloud 配置服务器与客户端

### 创建配置服务器

#### 增加依赖

```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

#### 开启配置服务器

```java
package com.sz.lyq.springcloudserverconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class SpringCloudServerConfigApplication {

   public static void main(String[] args) {
      SpringApplication.run(SpringCloudServerConfigApplication.class, args);
//    System.out.println(System.getProperty("user.dir"));
   }

}
```

#### 用GIT作为配置服务器

```properties
##应用名称
spring.application.name = spring-cloud-server-config
##配置服务器端口
server.port = 9090
##关闭actuator的安全模式及开放端点
management.endpoints.web.exposure.include=*
#配置服务器文件系统的git仓库
spring.cloud.config.server.git.uri = https://github.com/lyq93/tmp.git
spring.cloud.config.server.git.username = ***
spring.cloud.config.server.git.password = ***
##强制拉取git内容
spring.cloud.config.server.git.force-pull = true
```

#### GIT仓库的profile配置文件

![image-20200508140229103](/Users/liuyongqian/Library/Application Support/typora-user-images/image-20200508140229103.png)

### 创建客户端

#### 增加依赖

```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

#### 增加配置信息

```properties
##配置客户端应用关联的应用
##该项为配置服务器的默认配置文件的文件名
spring.cloud.config.name = serverConfig
##该项为取哪个环境的配置
spring.cloud.config.profile = test
##label
spring.cloud.config.label = master
##配置服务器uri
spring.cloud.config.uri = http://127.0.0.1:9090/
```

> 这个配置信息说明客户端取的配置服务器的配置信息是serverConfig-test.properties

#### 增加动态获取配置功能

```java
package com.sz.lyq.springcloudconfigclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Set;

@SpringBootApplication
@EnableScheduling
public class SpringCloudConfigClientApplication {
   //刷新配置服务器配置信息API
   private final ContextRefresher contextRefresher;
   //构造器注入
   @Autowired
   public SpringCloudConfigClientApplication(ContextRefresher contextRefresher) {
      this.contextRefresher = contextRefresher;
   }

   public static void main(String[] args) {
      SpringApplication.run(SpringCloudConfigClientApplication.class, args);
   }

   //刷新服务器配置
   @Scheduled(fixedRate = 1000L)
   public void update() {
      Set<String> keys = contextRefresher.refresh();

      if (!keys.isEmpty()) {
         System.out.println("该次更新的配置：" + keys);
      }
   }
}
```

> 1、开启调度功能
>
> 2、通过ContextRefresher API进行配置更新
>
> 当配置服务器的配置发生变化的时候，客户端能较及时的获取到相应的变化配置