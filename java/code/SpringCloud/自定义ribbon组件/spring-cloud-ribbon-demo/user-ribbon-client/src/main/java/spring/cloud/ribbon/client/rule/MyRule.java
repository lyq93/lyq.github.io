package spring.cloud.ribbon.client.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;

public class MyRule extends AbstractLoadBalancerRule {
    @Override
    public Server choose(Object key) {

        ILoadBalancer loadBalancer = getLoadBalancer();
        //获取可达的服务列表
        List<Server> reachableServers = loadBalancer.getReachableServers();

        if(reachableServers.isEmpty()) {
            return null;
        }

        Server server = reachableServers.get(reachableServers.size() - 1);

        return server;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {

    }
}
