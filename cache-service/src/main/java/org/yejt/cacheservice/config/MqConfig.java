package org.yejt.cacheservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig
{
    // Producer side
    public static final String CACHE_NODE_EXCHANGE = "cachenode.exchange.topic";

    public static final String ADD_NODE_TOPIC = "cache.node.add";

    public static final String REMOVE_NODE_TOPIC = "cache.node.remove";

    @Bean
    public TopicExchange exchange()
    {
        return new TopicExchange(CACHE_NODE_EXCHANGE, true, true);
    }

}
