package org.yejt.cacheroute.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class MqConfig
{
    // Consumer side
    public static final String CACHE_NODE_EXCHANGE = "cachenode.exchange.topic";

    public static final String ADD_NODE_QUEUE_NAME = "p-add-" + UUID.randomUUID();

    public static final String REMOVE_NODE_QUEUE_NAME = "p-remove-" + UUID.randomUUID();

    public static final String ADD_NODE_TOPIC = "cache.node.add";

    public static final String REMOVE_NODE_TOPIC = "cache.node.remove";

    @Bean
    public TopicExchange topicExchange()
    {
        return new TopicExchange(CACHE_NODE_EXCHANGE, true, true);
    }

    @Bean
    public Queue addNodeQueue()
    {
        return new Queue(ADD_NODE_QUEUE_NAME, true, false, true);
    }

    @Bean
    public Queue removeNodeQueue()
    {
        return new Queue(REMOVE_NODE_QUEUE_NAME, true, false, true);
    }

    @Bean
    public Binding addNodeBinding(Queue addNodeQueue, TopicExchange exchange)
    {
        return BindingBuilder.bind(addNodeQueue).to(exchange).with(ADD_NODE_TOPIC);
    }

    @Bean
    public Binding removeNodeBinding(Queue removeNodeQueue, TopicExchange exchange)
    {
        return BindingBuilder.bind(removeNodeQueue).to(exchange).with(REMOVE_NODE_TOPIC);
    }
}
