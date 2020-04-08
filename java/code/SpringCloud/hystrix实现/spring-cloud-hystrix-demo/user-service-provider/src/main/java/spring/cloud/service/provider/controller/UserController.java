package spring.cloud.service.provider.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.cloud.ribbon.api.UserService;
import spring.cloud.ribbon.domain.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

@RestController
public class UserController {

    private final Random random = new Random();

    @Autowired
    private UserService userService;

    @PostMapping("/user/save")
    public Boolean saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    /**
     * 这是服务端做hystrix熔断处理，hystrix也可以在客户端做处理
     * @return
     * @throws Exception
     */
    @HystrixCommand(commandProperties = { //设置超时时长
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "100")
    },
    fallbackMethod = "fallbackMethodHandler")
    @GetMapping("/user/list")
    public Collection<User> getUsers() throws Exception {
        //产生一个随机的时间
        long executeTime = random.nextInt(200);

        System.out.println("execute time:" + executeTime + " ms");

        Thread.sleep(executeTime);

        return userService.getUsers();
    }

    /**
     * 返回空集合
     * @return
     */
    public Collection<User> fallbackMethodHandler() {
        return Collections.emptyList();
    }
}
