package org.yejt.cacheroute.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.yejt.cacheroute.config.MqConfig;

@Component
public class ClusterNodeReceiver
{
    // TODO: Finish send join/leave message to MQ
    @RabbitListener(queues = "#{mqConfig.ADD_NODE_QUEUE_NAME}")
    public void receiveAddNode(Object o)
    {
        System.out.println(o);
    }

    @RabbitListener(queues = "#{mqConfig.REMOVE_NODE_QUEUE_NAME}")
    public void receiveRemoveNode(Object o)
    {
        System.out.println(o);
    }
}
