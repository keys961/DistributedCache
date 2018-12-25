package org.yejt.cacheservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yejt.cacheservice.cache.CacheManager;
import org.yejt.cacheservice.cache.manager.XXCacheManager;
import org.yejt.cacheservice.properties.CacheManagerProperties;

/**
 * @author keys961
 */
@Configuration
public class CacheServiceConfig {
    @Autowired
    private CacheManagerProperties cacheProperties;

    @Bean
    public CacheManager cacheManager() {
        return new XXCacheManager(cacheProperties);
    }
}
