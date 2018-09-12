package org.yejt.cacheroute.schedule;

import com.netflix.loadbalancer.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yejt.cacheroute.utils.ServiceMapUtils;

import java.util.Set;

@Component
public class HeartbeatSchedule
{
    //TODO: Finish heartbeat schedule
    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(initialDelay = 60000L, fixedDelayString = "${loadBalance.interval:120000}")
    public void heartBeatSchedule()
    {
        Set<Server> serverSet = ServiceMapUtils.getServerSet();
        serverSet.forEach(server ->
            {

            });

    }
}
