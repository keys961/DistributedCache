package org.yejt.cacheservice.mq;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yejt.cacheservice.config.MqConfig;

import javax.annotation.PostConstruct;

@Component
public class ClusterNodeSender implements ApplicationRunner, DisposableBean
{
    @Autowired
    private AmqpTemplate template;

    private InstanceInfo instanceInfo;

    public void sendAddNodeMessage()
    {
        // send registry message to proxy
        instanceInfo =
                ApplicationInfoManager.getInstance().getInfo();
        template.convertAndSend(MqConfig.CACHE_NODE_EXCHANGE,
                MqConfig.ADD_NODE_TOPIC, instanceInfo);
    }

    public void sendRemoveNodeMessage()
    {
        // send remove message to proxy
        template.convertAndSend(MqConfig.CACHE_NODE_EXCHANGE,
                MqConfig.REMOVE_NODE_TOPIC, instanceInfo);
    }

    @Override
    public void run(ApplicationArguments args)
    {
        sendAddNodeMessage();
    }

    @Override
    public void destroy() throws Exception
    {
        sendRemoveNodeMessage();
    }
}
