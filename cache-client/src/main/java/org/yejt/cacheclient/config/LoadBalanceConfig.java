package org.yejt.cacheclient.config;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LoadBalanceConfig
{
    @Bean
    public IRule iRule()
    {
        return new DHTRole();
    }

    /**
     * TODO: Implement DHT algorithm..
     */
    public static class DHTRole implements IRule
    {
        private ILoadBalancer iLoadBalancer;

        @Override
        public Server choose(Object o)
        {
            List<Server> serverList = iLoadBalancer.getReachableServers();
            if(serverList.size() == 0)
                return null;

            return serverList.get(0);
        }

        @Override
        public void setLoadBalancer(ILoadBalancer iLoadBalancer)
        {
            this.iLoadBalancer = iLoadBalancer;
        }

        @Override
        public ILoadBalancer getLoadBalancer()
        {
            return iLoadBalancer;
        }
    }
}
