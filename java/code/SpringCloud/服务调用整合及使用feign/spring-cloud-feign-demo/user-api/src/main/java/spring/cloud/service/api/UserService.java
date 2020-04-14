package spring.cloud.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import spring.cloud.service.domain.User;
import spring.cloud.service.fallback.UserServiceFallBack;

import java.util.List;

@FeignClient(name = "${user.service.name}", fallback = UserServiceFallBack.class) //使用占位符的方式避免硬编码
public interface UserService {

    @PostMapping("/user/save")
    public Boolean saveUser(User user);

    @GetMapping("/user/list")
    public List<User> getUsers();

}
