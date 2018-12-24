package org.yejt.cacheservice.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;
import org.yejt.cacheservice.service.CacheService;
import reactor.core.publisher.Mono;

@RestController
@SuppressWarnings("unchecked")
public class CacheEndpoint
{
    @Value("${server.port}")
    private int port;

    private CacheService cacheService;

    @Autowired
    public CacheEndpoint(CacheService cacheService)
    {
        this.cacheService = cacheService;
    }

    @GetMapping("/hello")
    public Mono<String> sayHello(@RequestParam(value = "name", defaultValue = "fucker") String name)
    {
        return Mono.just("hello," + name + "!" + " FROM: " + port);
    }

    @GetMapping(value = "/{cacheName}/{key}")
    public Mono get(@PathVariable String cacheName, @PathVariable String key)
    {
        Object cachedValue = cacheService.get(cacheName, key).orElse(null);
        if(cachedValue == null)
            return Mono.empty();
        return Mono.just(cachedValue);
    }

    /**
     * This method will cause cache overwritten
     */
    @PostMapping(value = "/{cacheName}/{key}")
    public Mono put(@PathVariable String cacheName, @PathVariable String key,
             @RequestBody Mono valuePublisher)
    {
        return Mono.create(emitter -> valuePublisher.subscribe(value ->
                cacheService.put(cacheName, key, value).ifPresent(emitter::success)));
    }

    @DeleteMapping(value = "/{cacheName}/{key}")
    public Mono remove(@PathVariable String cacheName, @PathVariable String key)
    {
        Object cachedValue = cacheService.remove(cacheName, key).orElse(null);
        if(cachedValue == null)
            return Mono.empty();
        return Mono.just(cachedValue);
    }

    @GetMapping(value = "/props")
    public Mono<CacheManagerProperties> getProperties()
    {
        return Mono.just(cacheService.getManagerProperties());
    }

    @GetMapping(value = "/props/{cacheName}")
    public Mono<CacheProperties> getCacheProperties(@PathVariable String cacheName)
    {
        return Mono.just(cacheService.getCacheProperties(cacheName));
    }

    @GetMapping(value = "/isClosed")
    public Mono<Boolean> isClosed()
    {
        return Mono.just(cacheService.isClosed());
    }

    @GetMapping(value = "/isClosed/{cacheName}")
    public Mono<Boolean> isClosed(@PathVariable String cacheName)
    {
        return Mono.just(cacheService.isClosed(cacheName));
    }
}
