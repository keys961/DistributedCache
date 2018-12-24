package org.yejt.cacheroute.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HeartbeatConfig
{
    @Bean
    public WebClient heartBeatWebClient()
    {
        return WebClient.create();
    }
}
