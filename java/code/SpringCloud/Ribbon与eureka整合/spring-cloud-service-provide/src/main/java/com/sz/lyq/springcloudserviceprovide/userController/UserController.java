package com.sz.lyq.springcloudserviceprovide.userController;

import com.sz.lyq.springcloudserviceprovide.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Value("${server.port}")
    private Integer serverPort;

    @PostMapping("/user")
    public String user(@RequestBody User user) {
        return user.toString() + "服务提供方应用端口：" + serverPort;
    }
}
