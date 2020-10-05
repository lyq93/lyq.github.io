#  SpringCloudEureka源码分析

## @EnableDiscoveryClient

> 一个应用要注册到服务中心或者是从服务中心获取服务列表，主要是需要做两件事情：
>
> - 在启动类打上@EnableDiscoveryClient的注解
> - 在application.properties中配置服务中心地址
>
> 那么，就从@EnableDiscoveryClient入手，查看该注解的注视，发现是开启DIscoveryClient的实例，通过类查找，发现一个spring自己提供的DiscoverClient的接口，一个是netflix提供的实现，通过查找上下结构，整理如下类的结构关系图

![image-20200925072327882](/Users/liuyongqian/Library/Application Support/typora-user-images/image-20200925072327882.png)

> 右边的LookupService、EurekaClient、DIscoveryClient都是netflix提供的，针对服务发现抽象的一些方法，而SpringCloud提供的DIscoverClient接口的好处是屏蔽了底层的实现细节，也就是意味着是使用eureka或是consul实现服务治理对于用户来说都是无感知的。
>
> 所以使用@EnableDiscoveryClient要比使用@EnableEurekaClient要好

## DiscoveryClient类

