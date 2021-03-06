package org.yejt.cacheroute.schedule;

import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.yejt.cacheroute.utils.ServiceMapUtils;

import java.net.URI;
import java.util.Set;

/**
 * @author keys961
 */
@Component
@EnableScheduling
public class HeartbeatSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatSchedule.class);
    private static final String PATH = "/health";
    private final WebClient heartBeatClient;

    @Autowired
    public HeartbeatSchedule(@Qualifier("heartBeatWebClient") WebClient heartBeatClient) {
        this.heartBeatClient = heartBeatClient;
    }

    @Scheduled(initialDelay = 30000L, fixedDelayString = "${loadBalance.interval:30000}")
    public void heartBeatSchedule() {
        Set<InstanceInfo> instanceInfoSet = ServiceMapUtils.getServerSet();
        instanceInfoSet.forEach(info -> {
            String ip = info.getIPAddr();
            int port = Integer.parseInt(info.getInstanceId().split(":")[2]);
            URI uri = URI.create("http://" + ip + ":" + port + PATH);
                    heartBeatClient.get().uri(uri)
                            .retrieve()
                            .bodyToMono(Integer.class)
                            .subscribe(status -> {
                                if (status != null) {
                                    if (status == HttpStatus.OK.value() || status == HttpStatus.NO_CONTENT.value()) {
                                        LOGGER.info("Server {} is still alive.", info.getInstanceId());
                                    } else {
                                        ServiceMapUtils.removeServerForcly(info);
                                        LOGGER.warn("Server {} is not alive.", info.getInstanceId());
                                    }
                                } else {
                                    ServiceMapUtils.removeServerForcly(info);
                                    LOGGER.warn("Server {} is not alive.", info.getInstanceId());
                                }
                            }, throwable -> {
                                ServiceMapUtils.removeServerForcly(info);
                                LOGGER.warn("Server {} is not alive.", info.getInstanceId());
                            });
                }
        );
    }
}
