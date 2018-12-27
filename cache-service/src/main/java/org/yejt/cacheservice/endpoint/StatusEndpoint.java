package org.yejt.cacheservice.endpoint;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Heartbeat REST API
 *
 * @author keys961
 */
@RestController
public class StatusEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusEndpoint.class);

    private final ApplicationInfoManager infoManager;

    @Autowired
    public StatusEndpoint(ApplicationInfoManager infoManager) {
        this.infoManager = infoManager;
    }

    @GetMapping(value = "/health")
    public Mono<Integer> health() {
        try {
            InstanceInfo myInfo = infoManager.getInfo();
            switch (myInfo.getStatus()) {
                case UP:
                    return Mono.just(HttpStatus.OK.value());
                case STARTING:
                    return Mono.just(HttpStatus.NO_CONTENT.value());
                case OUT_OF_SERVICE:
                    return Mono.just(HttpStatus.SERVICE_UNAVAILABLE.value());
                default:
                    return Mono.just(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Throwable throwable) {
            LOGGER.error("Error when doing health check: {}", throwable.getMessage());
            return Mono.just(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
