package spring.cloud.service.client.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.cloud.service.api.UserService;
import spring.cloud.service.domain.User;

import java.util.List;

@RestController
public class UserServiceClientController implements UserService {

    @Autowired
    private UserService userService;

    @Override
//    @PostMapping("/user/save")
    public Boolean saveUser(User user) {
        return userService.saveUser(user);
    }

    @Override
//    @GetMapping("/user/list")
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
