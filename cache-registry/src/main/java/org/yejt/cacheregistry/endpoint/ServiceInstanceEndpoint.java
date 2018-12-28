package org.yejt.cacheregistry.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * @author keys961
 */
@RestController
public class ServiceInstanceEndpoint {

    @GetMapping(value = "/service-instances/{appName}")
    public Flux getInstance(@PathVariable String appName) {
        //TODO: Fetch all instances, router scheduled fetch
        return Flux.empty();
    }

}
