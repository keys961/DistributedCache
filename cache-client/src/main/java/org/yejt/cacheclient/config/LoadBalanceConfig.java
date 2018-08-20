package org.yejt.cacheclient.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.zuul.ZuulFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yejt.cacheclient.utils.DHTLoadBalancer;

@Configuration
public class LoadBalanceConfig
{
    @Bean
    public IRule iRule()
    {
        return new DHTLoadBalancer();
    }


}
