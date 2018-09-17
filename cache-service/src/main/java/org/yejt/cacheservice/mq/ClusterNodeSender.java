package org.yejt.cacheservice.mq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yejt.cacheservice.config.MqConfig;

@Component
@EnableScheduling
public class ClusterNodeSender
{
    // TODO: Finish send join/leave message to MQ
    @Autowired
    private AmqpTemplate template;

    @Value("${server.port}")
    private int port;

    @Scheduled(fixedDelay = 10000L)
    public void sendAddNodeMessage()
    {
        String message = "Hello, I'm " + port;
        template.convertAndSend(MqConfig.CACHE_NODE_EXCHANGE,
                MqConfig.ADD_NODE_TOPIC, message);
    }

    @Scheduled(initialDelay = 10000L, fixedDelay = 5000L)
    public void sendRemoveNodeMessage()
    {
        String message = "Goodbye, I'm " + port;
        template.convertAndSend(MqConfig.CACHE_NODE_EXCHANGE,
                MqConfig.REMOVE_NODE_TOPIC, message);
    }
}
