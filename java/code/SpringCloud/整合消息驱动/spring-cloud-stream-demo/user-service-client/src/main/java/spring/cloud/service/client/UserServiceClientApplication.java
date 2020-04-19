package spring.cloud.service.client;

import com.netflix.loadbalancer.IRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import spring.cloud.service.api.UserService;
import spring.cloud.service.client.rule.MyRule;

@SpringBootApplication
@RibbonClient("user-service-provider")
@EnableFeignClients(clients = UserService.class) // 声明UserService作为feign client的调用
@EnableDiscoveryClient
public class UserServiceClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceClientApplication.class, args);
    }

    //暴露自定义rule
    @Bean
    public IRule myRule() {
        return new MyRule();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
