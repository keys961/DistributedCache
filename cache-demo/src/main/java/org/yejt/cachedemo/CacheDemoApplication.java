package org.yejt.cachedemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.yejt.cachedemo.entity.User;
import org.yejt.cachedemo.service.UserService;
import org.yejt.cachedemo.service.UserServiceRunner;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableFeignClients
public class CacheDemoApplication
{
    //TODO: fix up AOP problem
    @Autowired
    private UserServiceRunner runner;

    public static void main(String[] args)
    {
        SpringApplication.run(CacheDemoApplication.class, args);
    }
}
