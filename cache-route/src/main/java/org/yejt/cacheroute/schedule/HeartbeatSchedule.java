package org.yejt.cacheroute.schedule;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.yejt.cacheroute.utils.ServiceMapUtils;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@EnableScheduling
public class HeartbeatSchedule
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatSchedule.class);

    @Autowired
    private RestTemplate restTemplate;

    private static final String PATH = "/health";

    @Scheduled(initialDelay = 60000L, fixedDelayString = "${loadBalance.interval:60000}")
    public void heartBeatSchedule()
    {
        Set<InstanceInfo> instanceInfoSet = ServiceMapUtils.getServerSet();

        Set<InstanceInfo> removedSet = new HashSet<>();

        instanceInfoSet.forEach(info ->
            {
                String ip = info.getIPAddr();
                int port = Integer.parseInt(info.getInstanceId().split(":")[2]);
                URI uri = URI.create("http://" + ip + ":" + port + PATH);
                try
                {
                    ResponseEntity response = restTemplate.getForEntity(uri, Object.class);
                    HttpStatus status = response.getStatusCode();
                    if(status == HttpStatus.OK || status == HttpStatus.NO_CONTENT)
                        LOGGER.info("Server {} is still alive.", info.getInstanceId());
                    else
                    {
                        removedSet.add(info);
                        LOGGER.warn("Server {} is not alive.", info.getInstanceId());
                    }
                }
                catch (Exception e)
                {
                    removedSet.add(info);
                    LOGGER.warn("Server {} is not alive.", info.getInstanceId());
                }
            });

        ServiceMapUtils.removeServerForcely(removedSet);
    }
}
