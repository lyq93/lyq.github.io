## Spring Cloud Alibaba组件整合

### nacos注册中心与配置中心整合

### Spring Cloud Gateway整合

#### 整合步骤

#### 测试过程

#### 过程问题记录

> - 网关服务启动报dataSource相关错误
>
> 原因：是由于网关服务依赖了公共服务，公共服务引用了mybatis相关的jar包导致
>
> 解决方案：
>
> 1、可以通过pom文件去除不需要的依赖
>
> 2、通过SpringBootApplication注解的exclude方法去除DataSourceAutoConfiguration类
>
> - 网关服务启动报错：The elements [spring.cloud.gateway.routes[0].predicates[0].path] were left unbound.
>
> 原因：
>
> > ```properties
> > gateway:
> >   routes:
> >     - id: after_route
> >       uri: lb://renren-fast
> >       predicates:
> >         - Path: /api/**
> > ```
>
> path配置使用了空格的原因
>
> 解决方案：
>
> 去掉空格
>
> - 网关服务报错：
>
> > Failed to bind properties under 'spring.cloud.gateway.routes[0].predicates[0]' to org.springframework.cloud.gateway.handler.predicate.PredicateDefinition:
> >
> > Property: spring.cloud.gateway.routes[0].predicates[0]
> > Value: Path:/api/**
> > Origin: class path resource [application.yml]:13:15
> > Reason: failed to convert java.lang.String to org.springframework.cloud.gateway.handler.predicate.PredicateDefinition
> >
> > Action:
> >
> > Update your application's configuration
>
> 原因：
>
> > ```properties
> > routes:
> >   - id: after_route
> >     uri: lb://renren-fast
> >     predicates:
> >       - Path:/api/**
> > ```
>
> path配置需要使用等号而不是冒号
>
> - 网关服务正常启动，前端服务访问报404
>
> > ```properties
> > routes:
> >   - id: after_route
> >     uri: lb://renren-fast
> >     predicates:
> >       - Path=/api/**
> > ```
>
> 原因：
>
> 前端请求路径：http://localhost:88/api/captcha.jpg
>
> 分析网关配置，拦截到请求之后，转发的地址应该是renren-fast:端口/captcha.jpg，
>
> 这个请求路径是不对的，renren-fast服务还有一个context-path
>
> 解决方案：
>
> 使用RewritePath，配置如下
>
> > ```properties
> > routes:
> >   - id: after_route
> >     uri: lb://renren-fast
> >     predicates:
> >       - Path=/api/**
> >     filters:
> >           - RewritePath=/api/?(?<segment>.*), /renren-fast/$\{segment}
> > ```
>
> - 前端访问报跨域问题
>
> > Access to XMLHttpRequest at 'http://localhost:88/api/sys/login' from origin 'http://localhost:8001' has been blocked by CORS policy: Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present on the requested resource.
>
> 原因：由本机服务8001访问本机服务88属于非同源请求，所以出现跨域问题
>
> 解决方案：
>
> 在网关服务编写一个跨域配置类，允许跨域请求
>
> 核心代码如下：
>
> > ```java
> > @Configuration
> > public class MallCorsConfiguration {
> > 
> >     @Bean
> >     public CorsWebFilter corsWebFilter() {
> >         UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
> >         CorsConfiguration corsConfiguration = new CorsConfiguration();
> >         corsConfiguration.setAllowCredentials(true);
> >         corsConfiguration.addAllowedHeader("*");
> >         corsConfiguration.addAllowedMethod("*");
> >         corsConfiguration.addAllowedOrigin("*");
> >         urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
> >         return new CorsWebFilter(urlBasedCorsConfigurationSource);
> >     }
> > 
> > }
> > ```
>
> - 前端服务请求报跨域问题
>
> > Access to XMLHttpRequest at 'http://localhost:88/api/sys/login' from origin 'http://localhost:8001' has been blocked by CORS policy: The 'Access-Control-Allow-Origin' header contains multiple values 'http://localhost:8001, http://localhost:8001', but only one is allowed.
>
> 原因：根据报错信息分析，可能在网关需要转发请求过去的那个服务中也存在解决跨域问题的配置类，存在多个类导致
>
> 解决方案：
>
> 跨域问题统一由网关处理，各服务之间删除解决跨域问题的配置类

### 文件上传服务整合

#### 针对OSS文件上传实现的前提条件

- 注册阿里云账号
- 开通对象存储OSS
- 申请AccessKey
- 创建bucket--可以理解为存储文件的容器
- 设置跨域访问--允许请求所有访问

#### 针对OSS文件上传的实现

- 简单文件上传

  - 服务依赖

  > ```properties
  > <dependency>
  >    <groupId>com.aliyun.oss</groupId>
  >    <artifactId>aliyun-sdk-oss</artifactId>
  >    <version>3.10.2</version>
  > </dependency>
  > ```

  - 服务实现

  > ```java
  > // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
  > String endpoint = "xxxx";
  > // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
  > String accessKeyId = "xxx";
  > String accessKeySecret = "xxx";
  > 
  > // 创建OSSClient实例。
  > OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
  > 
  > // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
  > InputStream inputStream = new FileInputStream("/Users/liuyongqian/Downloads/xx.doc");
  > System.out.println(inputStream.available());
  > // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
  > ossClient.putObject("mall-product-lyq", "xx.doc", inputStream);
  > 
  > // 关闭OSSClient。
  > ossClient.shutdown();
  > ```

  - 实现弊端

  > 所有上传请求都要经过服务器，增加服务器压力

- 基于spring cloud alibaba的实现

  - 实现思路

    > 1、使用OSS提供的WEB端直传的其中一种方式--服务端签名后直传
    >
    > 用户使用表单选择完文件后，前端调用后端获取上传文件签名接口，拿到上传文件所需要的相关信息，而后由前端直接发起文件上传请求
    >
    > 2、把整合第三方服务的功能抽离到一个单独的服务中，包括后续可能存在的一些其他第三方服务，都在该服务下进行对接整合

  - 依赖

    > ```properties
    > <dependency>
    >     <groupId>com.alibaba.cloud</groupId>
    >     <artifactId>spring-cloud-alicloud-oss</artifactId>
    >     <version>2.1.1.RELEASE</version>
    > </dependency>
    > ```

  - 实现

    > ```java
    > package com.sz.mall.third.oss;
    > 
    > import com.aliyun.oss.OSS;
    > import com.aliyun.oss.common.utils.BinaryUtil;
    > import com.aliyun.oss.model.MatchMode;
    > import com.aliyun.oss.model.PolicyConditions;
    > import com.sz.mall.common.utils.R;
    > import org.springframework.beans.factory.annotation.Autowired;
    > import org.springframework.beans.factory.annotation.Value;
    > import org.springframework.web.bind.annotation.GetMapping;
    > import org.springframework.web.bind.annotation.RestController;
    > 
    > import java.time.LocalDate;
    > import java.time.format.DateTimeFormatter;
    > import java.util.Date;
    > import java.util.LinkedHashMap;
    > import java.util.Map;
    > 
    > @RestController
    > public class OssController {
    >     @Value("${spring.cloud.alicloud.access-key}")
    >     private String accessId;
    >     @Value("${spring.cloud.alicloud.secret-key}")
    >     private String accessKey;
    >     @Value("${spring.cloud.alicloud.oss.endpoint}")
    >     private String endPoint;
    >     @Value("${spring.cloud.alicloud.oss.bucketName}")
    >     private String bucketName;
    >     @Autowired
    >     private OSS ossClient;
    > 
    >     /**
    >      * 获取上传文件的签名
    >      *
    >      * @return
    >      */
    >     @GetMapping("/oss/policy")
    >     public R getPolicy() {
    >         // host的格式为 bucketname.endpoint
    >         String host = "https://" + bucketName + "." + endPoint;
    >         // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
    >         String dir = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/"; // 用户上传文件时指定的前缀。
    > 
    >         // 创建OSSClient实例。
    > //        OSS ossClient = new OSSClientBuilder().build(endPoint, accessId, accessKey);
    >         Map<String, String> respMap = new LinkedHashMap<String, String>();
    >         try {
    >             long expireTime = 30;
    >             long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
    >             Date expiration = new Date(expireEndTime);
    >             // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
    >             PolicyConditions policyConds = new PolicyConditions();
    >             policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
    >             policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
    > 
    >             String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
    >             byte[] binaryData = postPolicy.getBytes("utf-8");
    >             String encodedPolicy = BinaryUtil.toBase64String(binaryData);
    >             String postSignature = ossClient.calculatePostSignature(postPolicy);
    > 
    >             respMap.put("accessId", accessId);
    >             respMap.put("policy", encodedPolicy);
    >             respMap.put("signature", postSignature);
    >             respMap.put("dir", dir);
    >             respMap.put("host", host);
    >             respMap.put("expire", String.valueOf(expireEndTime / 1000));
    >         } catch (Exception e) {
    >             // Assert.fail(e.getMessage());
    >             System.out.println(e.getMessage());
    >         } finally {
    >             ossClient.shutdown();
    >         }
    >         return R.ok().put("data", respMap);
    >     }
    > }
    > ```

  - 文件服务上传功能实现中遇到的问题：

    - 服务启动失败，异常消息：OSS endpoint is empty

      > 原因：
      >
      > Spring cloud alibaba组件的整合多是参考github上的开源中文文档进行开发，针对OSS这块Github上的配置示例如下：
      >
      > alibaba.cloud.access-key: xxxxxxxxxxx
      >
      > alibaba.cloud.secret-key: xxxxxxxxxxx
      >
      > alibaba.cloud.endpoint: xxxxxxxxxxx
      >
      > 根据报错信息，猜到配置没有生效，原因在于该配置为老版本配置。经查资料，最终新版本配置如下：
      >
      > ```properties
      > alicloud:
      >     access-key: xx
      >     secret-key: xx
      >     oss:
      >       endpoint: oss-cn-shenzhen.aliyuncs.com
      >       bucketName: mall-product-lyq
      > ```

    - 前端获取签名成功，请求文件上传失败，控制台提示跨域问题

      > 这点已经在最开始的前提条件中列出。由于前端调用文件上传是不经过自己的网关服务器的，所以一定会存在跨域问题。在阿里云的OSS的基本设置中需要开启跨域访问

      

### 分布式事务解决方案seata的整合使用

#### 环境准备

- seata服务器准备

> 通过docker下拉seataServer，通过最简单的命令运行起来
>
> docker run --name seata-server -p 8091:8091 seataio/seata-server
>
> 以交互式进入到容器，拿到resource目录下的file.conf和registry.conf文件，放到宿主机的某个目录下准备挂载
>
> docker exec -it xxxx /bin/bash
>
> 修改registry文件，修改注册中心类型为nacos，还有一些nacos的配置信息，目前我使用的是只有注册中心使用的是nacos，其他都是使用默认的
>
> registry {
>
>  \# file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
>
>  type = "nacos"
>
> nacos {
>
>   application = "serverAddr"
>
>   serverAddr = "120.25.251.133:8848"
>
>   group = "DEFAULT_GROUP"
>
>   namespace = ""
>
>   cluster = "default"
>
>   username = "nacos"
>
>   password = "nacos"
>
>  }
>
> }
>
> 删除原容器，重新创建并启动容器，这次挂载目录以及注意指定seata服务的外网ip
>
> docker run --name seata-server \
>         -p 8091:8091 \
>         -e SEATA_IP=47.106.171.169 \
>         -e SEATA_CONFIG_NAME=file:/root/seata-config/registry \
>         -v /mydata/seata/config:/root/seata-config  \
>         seataio/seata-server

- 业务数据库环境准备

> 在各个需要用到分布式事务的服务对应的库上创建undo_log表（官网有执行sql，不用手写）
>
> *-- 注意此处0.3.0+ 增加唯一索引 ux_undo_log* 
>
> **CREATE** **TABLE** `undo_log` (  `id` bigint(20) **NOT** NULL AUTO_INCREMENT,  `branch_id` bigint(20) **NOT** NULL,  `xid` varchar(100) **NOT** NULL,  `context` varchar(128) **NOT** NULL,  `rollback_info` longblob **NOT** NULL,  `log_status` int(11) **NOT** NULL,  `log_created` datetime **NOT** NULL,  `log_modified` datetime **NOT** NULL,  `ext` varchar(100) **DEFAULT** NULL,  PRIMARY **KEY** (`id`),  **UNIQUE** **KEY** `ux_undo_log` (`xid`,`branch_id`) ) **ENGINE**=**InnoDB** AUTO_INCREMENT=1 **DEFAULT** **CHARSET**=utf8;

- 服务引入springCloudAlibabSeata依赖

> 各服务引入spring cloud alibaba starter seata服务，配置seata nacos配置，可通过查看*autoConfiguration找到对应的配置实体类
>
> ```properties
> <dependency>
>     <groupId>com.alibaba.cloud</groupId>
>     <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
>     <version>2.2.0.RELEASE</version>
> </dependency>
> ```
>
> seata:
>   registry:
>     type: nacos
>     nacos:
>       server-addr: 120.25.251.133:8848

#### 分布式事务功能测试

- 测试代码介绍

> 准备三个服务，ABC。A服务通过feign调用B服务的方法B，B方法执行插入操作。A服务通过feign调用C服务的方法C，C方法主要用来抛出异常

- 不添加分布式事务注解的情况下进行测试

> 操作：
>
> 通过调用A服务的A方法，B服务的数据正常插入，C服务抛出异常，A服务进行本地事务回滚
>
> 结果：
>
> A服务的数据回滚，B服务的数据未回滚，存在分布式事务问题

- 在A服务的A方法添加@GlobalTransaction注解进行分布式事务控制

> 操作：
>
> 通过调用A服务的A方法，B服务的数据正常插入，C服务抛出异常，A服务进行本地事务回滚
>
> 结果：
>
> 通过断点发现，当B服务正常插入数据时，undo_log表里面有一条数据，数据包括branch_id，xid，rollback_info等关键字段
>
> 当A服务抛出异常回滚时，B服务的undo_log表的时候没有了，B服务的数据也回滚了，分布式事务问题解决

#### 分布式事务解决方案原理分析

> 针对Seata的原理分析：
>
> 1、seata主要有三个组件，TC、TM、RM
>
> TC：可以理解为分布式事务的全局协调器，即seata服务
>
> TM：分布式事务的管理者，负责开启分布式事务及提交最终的全局事务信息给TC
>
> RM：本地事务的管理者，主要负责本地事务的提交与注册事务信息给TC
>
> 
>
> seata分布式事务的处理流程：
>
> 1、首先打上@GlobalTransaction注解的是TM，TM会向seata服务发起一个分布式事务的请求，seata服务会返回一个xid的全局事务ID
>
> 2、然后xid会跟随着服务的调用链进行传递，RM进行本地事务提交的同时，也会把需要进行回滚的信息写到undo_log表中，包括通过链路传递的xid，并且会向seata服务注册自己
>
> 3、当某个服务出现异常的时候，注册给seata服务器的事务信息为不成功，这个时候seata服务器会统一进行回滚，利用undo_log中的信息
>
> 4、当所有服务的事务都正常提交的时候，seata服务器会统一进行提交并删除undo_log日志

