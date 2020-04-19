package spring.cloud.service.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.stream.annotation.EnableBinding;
import spring.cloud.service.provider.stream.UserMessage;

@SpringBootApplication
@EnableHystrix
@EnableBinding(value = UserMessage.class)
public class UserServiceProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceProviderApplication.class, args);
    }

}
