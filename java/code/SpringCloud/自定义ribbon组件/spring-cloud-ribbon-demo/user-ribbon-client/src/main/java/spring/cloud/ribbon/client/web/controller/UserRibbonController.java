package spring.cloud.ribbon.client.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import spring.cloud.ribbon.domain.User;

@RestController
public class UserRibbonController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Value("${provider.service.name}")
    private String serviceProvider;

    @GetMapping("")
    public String index() throws Exception {
        //通过loadBalancerClientAPI选择一台服务
        ServiceInstance serviceInstance = loadBalancerClient.choose(serviceProvider);

        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setAge(0);
        //执行方法调用
        return loadBalancerClient.execute(serviceProvider,serviceInstance, instance -> {
            String host = instance.getHost();
            int port = instance.getPort();
            //拼接url
            String url = "http://" + host + ":" + port + "/user/save";

            RestTemplate restTemplate = new RestTemplate();
            //通过RestTemplate发起请求
            return restTemplate.postForObject(url , user, String.class);

        });
    }

}
