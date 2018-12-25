package org.yejt.cacheroute.config;

import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yejt.cacheroute.route.DHTLoadBalancerRule;

/**
 * @author keys961
 */
@Configuration
public class RouteConfig {
    @Value("${loadBalance.virtualNode:1}")
    private int virtualNodeCount;

    @Bean
    public IRule loadBalancerRule() {
        return new DHTLoadBalancerRule(virtualNodeCount);
    }
}
