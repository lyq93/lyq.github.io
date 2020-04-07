package spring.cloud.ribbon.client;

import com.netflix.loadbalancer.IRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import spring.cloud.ribbon.client.rule.MyRule;

@SpringBootApplication
@RibbonClient("user-service-provider")
public class UserRibbonClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserRibbonClientApplication.class, args);
    }

    //暴露自定义rule
    @Bean
    public IRule myRule() {
        return new MyRule();
    }

}
