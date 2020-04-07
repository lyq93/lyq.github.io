package spring.cloud.service.provider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.cloud.ribbon.api.UserService;
import spring.cloud.ribbon.domain.User;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/save")
    public Boolean saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
}
