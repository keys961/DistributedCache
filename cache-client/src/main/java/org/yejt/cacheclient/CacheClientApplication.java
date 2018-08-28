package org.yejt.cacheclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

/**
 * This class may be deleted, just offered a client to
 * access the cache cluster.
 * These @EnableXXX annotations & application.yml configurations
 * must be added to the upper application using this caching service
 */
public class CacheClientApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(CacheClientApplication.class, args);
    }
}
