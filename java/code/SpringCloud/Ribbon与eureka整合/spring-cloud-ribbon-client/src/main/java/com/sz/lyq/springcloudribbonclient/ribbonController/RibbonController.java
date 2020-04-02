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
