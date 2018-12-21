package org.yejt.cacheservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * This application provides the real caching functions.
 * It will be deployed to a server and registered to the
 * registry server.
 * 1. client -> registry server (with load balance on client): get caching server
 * 2. client -> caching server: operate on cache
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
public class CacheServiceApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(CacheServiceApplication.class, args);
    }
}
