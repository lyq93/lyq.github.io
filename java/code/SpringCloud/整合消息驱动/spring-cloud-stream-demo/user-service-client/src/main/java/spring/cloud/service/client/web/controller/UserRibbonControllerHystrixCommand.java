package spring.cloud.service.client.web.controller;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;

/**
 * 客户端实现hystrix
 */
public class UserRibbonControllerHystrixCommand extends HystrixCommand<Collection> {

    private String providerServiceName;
    private final RestTemplate restTemplate;

    public UserRibbonControllerHystrixCommand(String providerServiceName, RestTemplate restTemplate) {
        super(HystrixCommandGroupKey.Factory.asKey("user-ribbon-client"),
                100);
        this.providerServiceName = providerServiceName;
        this.restTemplate = restTemplate;
    }

    /**
     * 主要执行逻辑
     * @return
     * @throws Exception
     */
    @Override
    protected Collection run() throws Exception {
        String url = "http://" + providerServiceName + "/user/list";
        return restTemplate.getForObject(url,Collection.class);
    }

    /**
     * 异常回调
     * @return
     */
    @Override
    protected Collection getFallback() {
        return Collections.emptyList();
    }
}
