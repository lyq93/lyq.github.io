package com.sz.lyq.springcloudeurekaclient.controller;

import com.netflix.discovery.shared.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 问题：在配置好配置服务器并注册到eureka配置中心时，eureka客户端启动并注册之后获取不到配置服务器的配置信息
 * 通过使用DiscoveryClient类确认服务发现是正常的，启动也并没有报任务错误，但是通过personController对person的
 * 访问，返回的属性值不对。
 * 原因在于：没有把eureka客户端服务作为config的客户端，即需要引入
 * <dependency>
 *   <groupId>org.springframework.cloud</groupId>
 *   <artifactId>spring-cloud-starter-config</artifactId>
 * </dependency>
 */
@RestController
public class DiscoveryController {

    private final DiscoveryClient discoveryClient;

    @Autowired
    public DiscoveryController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/getConfigServerName")
    public String getServices() {
        List<String> services = discoveryClient.getServices();
        return services.toString();
    }
}
