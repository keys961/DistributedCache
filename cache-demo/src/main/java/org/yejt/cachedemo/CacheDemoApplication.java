package org.yejt.cachedemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.yejt.cacheclient.annotation.XXCacheAutoConfiguration;
import org.yejt.cachedemo.service.UserServiceRunner;

/**
 * @author keys961
 */
@SpringBootApplication
@XXCacheAutoConfiguration
@EnableDiscoveryClient
@EnableEurekaClient
public class CacheDemoApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheDemoApplication.class);

    private final UserServiceRunner runner;

    @Autowired
    public CacheDemoApplication(UserServiceRunner runner) {
        this.runner = runner;
        init();
    }

    public static void main(String[] args) {
        SpringApplication.run(CacheDemoApplication.class, args);
    }

    private void init() {
        LOGGER.info("App initialized: {}.", runner.toString());
    }
}
