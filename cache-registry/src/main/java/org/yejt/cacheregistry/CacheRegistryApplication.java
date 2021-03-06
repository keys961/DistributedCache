package org.yejt.cacheregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * <p>This application is used as a registry server
 * which manages the cluster cache server.
 * <p>Cache servers provide the cache service.
 *
 * @author yejt
 */
@SpringBootApplication
@EnableEurekaServer
@EnableDiscoveryClient
public class CacheRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheRegistryApplication.class, args);
    }
}
