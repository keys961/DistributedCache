package org.yejt.cacheroute.config;

import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.yejt.cacheroute.route.DHTLoadBalancerRule;

/**
 * @author keys961
 */
@Configuration
public class RouteConfig {
    @Value("${loadBalance.virtualNode:1}")
    private int virtualNodeCount;

    @Value("${eureka.client.serviceUrl.registryUrl:}")
    private String registryUrl;

    @Bean
    public IRule loadBalancerRule() {
        return new DHTLoadBalancerRule(virtualNodeCount);
    }

    @Bean(value = "registryWebClient")
    public WebClient registryWebClient() {
        return WebClient.builder().baseUrl(registryUrl).build();
    }
}
