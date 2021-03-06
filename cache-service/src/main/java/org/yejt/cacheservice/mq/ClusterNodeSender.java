package org.yejt.cacheservice.mq;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.yejt.cacheservice.config.MqConfig;
import org.yejt.cacheservice.utils.TimestampUtils;

import java.io.IOException;

/**
 * @author keys961
 */
@Component
public class ClusterNodeSender implements ApplicationRunner, DisposableBean {
    private static final String TIMESTAMP = "timestamp";

    private final AmqpTemplate template;

    private final ApplicationInfoManager infoManager;

    @Autowired
    public ClusterNodeSender(AmqpTemplate template, ApplicationInfoManager infoManager) {
        this.template = template;
        this.infoManager = infoManager;
    }

    public void sendAddNodeMessage() {
        // send registry message to proxy
        InstanceInfo instanceInfo =
                infoManager.getInfo();
        template.convertAndSend(MqConfig.CACHE_NODE_EXCHANGE,
                MqConfig.ADD_NODE_TOPIC, instanceInfo,
                this::sendMessage);
    }

    public void sendRemoveNodeMessage() {
        // send remove message to proxy
        InstanceInfo instanceInfo =
                infoManager.getInfo();
        template.convertAndSend(MqConfig.CACHE_NODE_EXCHANGE,
                MqConfig.REMOVE_NODE_TOPIC, instanceInfo,
                this::sendMessage);
    }

    private Message sendMessage(Message message) {
        try {
            message.getMessageProperties().setHeader(TIMESTAMP,
                    TimestampUtils.getTimestamp(infoManager.getInfo()));
        } catch (IOException e) {
            message.getMessageProperties().setHeader(TIMESTAMP, 0);
        }
        return message;
    }

    @Override
    public void run(ApplicationArguments args) {
        sendAddNodeMessage();
    }

    @Override
    public void destroy() {
        sendRemoveNodeMessage();
    }
}
