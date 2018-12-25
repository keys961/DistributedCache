package org.yejt.cacheservice.mq;

import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.yejt.cacheservice.utils.ServiceMapUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author keys961
 */
@Component
public class NodeMapReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeMapReceiver.class);

    @RabbitListener(queues = "#{mqConfig.NODE_MAP_QUEUE_NAME}")
    public void receiveNodeMap(@Payload TreeMap<Integer, InstanceInfo> nodeMap, @Header long version) {
        LOGGER.info("Receive node map with version {}: {}", version,
                nodeMap);
        Set<InstanceInfo> serverSet = new HashSet<>();
        nodeMap.forEach((k, v) -> serverSet.add(v));
        ServiceMapUtils.updateServerMap(nodeMap, serverSet, version);
    }
}
