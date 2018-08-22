package org.yejt.cacheservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yejt.cacheservice.properties.CacheProperties;
import org.yejt.cacheservice.service.CacheService;
import org.yejt.cacheservice.service.CacheServiceBuilder;

@Configuration
public class CacheServiceConfig
{
    //TODO: build with configurations
    @Autowired
    private CacheProperties cacheProperties;

    @Bean
    public CacheServiceBuilder cacheServiceBuilder()
    {
        return new CacheServiceBuilder();
    }

    @Bean
    public CacheService cacheService(CacheServiceBuilder builder)
    {
        return builder.build();
    }
}
