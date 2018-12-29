package org.yejt.cacheroute.schedule;

import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.yejt.cacheroute.utils.ConsistentHash;
import org.yejt.cacheroute.utils.ServiceMapUtils;

import java.util.TreeMap;

/**
 * @author keys961
 */
@Component
public class NodeMapSchedule implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeMapSchedule.class);
    private final WebClient registryClient;
    @Value("${loadBalance.virtualNode:16}")
    private int virtualNodes;

    @Autowired
    public NodeMapSchedule(@Qualifier(value = "registryWebClient") WebClient registryClient) {
        this.registryClient = registryClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        TreeMap<Integer, InstanceInfo> infoTreeMap = new TreeMap<>();
        registryClient.get().retrieve().bodyToFlux(InstanceInfo.class)
                .filter(instanceInfo -> instanceInfo.getStatus() == InstanceInfo.InstanceStatus.UP)
                .subscribe(instanceInfo -> {
                            int port = Integer.parseInt(instanceInfo.getInstanceId().split(":")[2]);
                            for (int i = 0; i < virtualNodes; i++) {
                                String tag = "#" + i + "-"
                                        + instanceInfo.getIPAddr() + ":" + port
                                        + "::" + instanceInfo.getAppName();
                                int hashVal = ConsistentHash.getHash(tag);
                                infoTreeMap.put(hashVal, instanceInfo);
                            }
                        }, throwable -> LOGGER.error("Initial fetch node map failed. Msg: {}",
                        throwable.getMessage())
                        , () -> {
                            if (!infoTreeMap.isEmpty()) {
                                ServiceMapUtils.setServerTreeMap(infoTreeMap);
                            }
                            LOGGER.info("Initial fetch node complete. {} instance(s) UP.", infoTreeMap.size());
                        });
    }
}
