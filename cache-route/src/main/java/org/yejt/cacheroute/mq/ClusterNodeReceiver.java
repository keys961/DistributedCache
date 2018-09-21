package org.yejt.cacheroute.mq;

import com.netflix.appinfo.InstanceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.yejt.cacheroute.utils.ConsistentHash;
import org.yejt.cacheroute.utils.ServiceMapUtils;

import java.util.HashSet;

@Component
public class ClusterNodeReceiver
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterNodeReceiver.class);

    @Value("${loadBalance.virtualNode:16}")
    private int virtualNodes;

    @RabbitListener(queues = "#{mqConfig.ADD_NODE_QUEUE_NAME}")
    public void receiveAddNode(@Payload InstanceInfo o)
    {
        // registry
        HashSet<Integer> hashValSet = new HashSet<>();
        int port = Integer.parseInt(o.getInstanceId().split(":")[2]);
        for(int i = 0; i < virtualNodes; i++)
        {
            String tag = "#" + i + "-"
                    + o.getIPAddr() + ":" + port
                    + "::" + o.getAppName();
            int hashVal = ConsistentHash.getHash(tag);
            hashValSet.add(hashVal);
        }
        ServiceMapUtils.addServer(hashValSet, o);
        LOGGER.info("Server added: {}.", o.getInstanceId());
    }

    @RabbitListener(queues = "#{mqConfig.REMOVE_NODE_QUEUE_NAME}")
    public void receiveRemoveNode(@Payload InstanceInfo o)
    {
        // remove
        ServiceMapUtils.removeServer(o);
        LOGGER.info("Server removed: {}.", o.getInstanceId());
    }
}
