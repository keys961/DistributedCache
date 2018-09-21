package org.yejt.cacheservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig
{
    //TODO: Data migration may be added in the future..
    // Producer side
    public static final String CACHE_NODE_EXCHANGE = "cachenode.exchange.topic";

    public static final String ADD_NODE_TOPIC = "cache.node.add";

    public static final String REMOVE_NODE_TOPIC = "cache.node.remove";

    @Bean
    public TopicExchange exchange()
    {
        return new TopicExchange(CACHE_NODE_EXCHANGE, true, true);
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter()
    {
        // sender
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory)
    {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }
}
