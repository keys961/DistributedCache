package org.yejt.cacheregistry.endpoint;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * @author keys961
 */
@RestController
public class ServiceInstanceEndpoint {

    private final PeerAwareInstanceRegistry registry;

    @Autowired
    public ServiceInstanceEndpoint(PeerAwareInstanceRegistry registry) {
        this.registry = registry;
    }

    @GetMapping(value = "/service-instances/{appName}")
    public Flux<InstanceInfo> getInstance(@PathVariable String appName) {
        Application targetApplication = registry.getApplications()
                .getRegisteredApplications(appName);
        if (targetApplication == null) {
            return Flux.empty();
        }
        return Flux.fromIterable(targetApplication.getInstances());
    }

}
