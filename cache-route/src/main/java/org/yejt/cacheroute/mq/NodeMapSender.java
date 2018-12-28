package org.yejt.cacheroute.mq;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yejt.cacheroute.config.MqConfig;
import org.yejt.cacheroute.utils.ServiceMapUtils;

import java.util.TreeMap;

/**
 * @author keys961
 */
@Component
public class NodeMapSender {
    private static final String VERSION = "version";

    private final RabbitTemplate template;

    @Autowired
    public NodeMapSender(RabbitTemplate template) {
        this.template = template;
    }

    public void sendNodeMap() {
        TreeMap<Integer, InstanceInfo> nodeMap = ServiceMapUtils.getServerTreeMap();
        long version = ServiceMapUtils.getVersion();

        template.convertAndSend(MqConfig.CACHE_NODE_EXCHANGE,
                MqConfig.NODE_MAP_TOPIC, nodeMap,
                message -> {
                    message.getMessageProperties().setHeader(VERSION,
                            version);
                    return message;
                });
    }

}
