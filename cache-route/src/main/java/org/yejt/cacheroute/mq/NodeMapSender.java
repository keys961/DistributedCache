package org.yejt.cacheroute.mq;

import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yejt.cacheroute.utils.ServiceMapUtils;

import java.util.TreeMap;

@Component
public class NodeMapSender
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeMapSender.class);

    @Autowired
    private RabbitTemplate template;

    //TODO: To spread the map to all the server..
    public void sendNodeMap()
    {
        TreeMap<Integer, InstanceInfo> nodeMap = ServiceMapUtils.getServerTreeMap();
        long version = ServiceMapUtils.getVersion();



    }

}
