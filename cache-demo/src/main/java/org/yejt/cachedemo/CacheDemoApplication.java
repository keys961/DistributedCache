package org.yejt.cachedemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.yejt.cacheclient.annotation.XXCacheAutoConfiguration;
import org.yejt.cachedemo.service.UserServiceRunner;

@SpringBootApplication
@XXCacheAutoConfiguration
@EnableDiscoveryClient
@EnableEurekaClient
public class CacheDemoApplication
{
    @Autowired
    private UserServiceRunner runner;

    public static void main(String[] args)
    {
        SpringApplication.run(CacheDemoApplication.class, args);
    }
}
