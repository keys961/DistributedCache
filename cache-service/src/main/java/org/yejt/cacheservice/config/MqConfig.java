package org.yejt.cacheservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import java.util.UUID;

/**
 * @author keys961
 */
@Configuration
public class MqConfig implements RabbitListenerConfigurer {
    //TODO: Data migration, replication may be added in the future..
    /**
     * Producer side
     */
    public static final String CACHE_NODE_EXCHANGE = "cachenode.exchange.topic";

    public static final String ADD_NODE_TOPIC = "cache.node.add";

    public static final String REMOVE_NODE_TOPIC = "cache.node.remove";

    /**
     * Consumer side, multi-casting hash map
     */
    public static final String NODE_MAP_QUEUE_NAME = "p-map-" + UUID.randomUUID();


    public static final String NODE_MAP_TOPIC = "cache.node.map";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(CACHE_NODE_EXCHANGE, true, true);
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        // sender
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    /**
     * Consumer
     */
    @Bean
    public Queue nodeMapQueue() {
        return new Queue(NODE_MAP_QUEUE_NAME, true, false, true);
    }

    @Bean
    public Binding nodeMapBinding(Queue nodeMapQueue, TopicExchange exchange) {
        return BindingBuilder.bind(nodeMapQueue).to(exchange).with(NODE_MAP_TOPIC);
    }

    @Bean
    public MessageHandlerMethodFactory
    messageHandlerMethodFactory(MappingJackson2MessageConverter converter) {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory =
                new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(converter);
        return messageHandlerMethodFactory;
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        rabbitListenerEndpointRegistrar.setMessageHandlerMethodFactory(
                messageHandlerMethodFactory(consumerJackson2MessageConverter()));
    }

}
