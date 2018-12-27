package org.yejt.cacheservice.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;
import org.yejt.cacheservice.service.CacheService;
import reactor.core.publisher.Mono;

/**
 * Main cache service REST API
 *
 * @author keys961
 */
@RestController
public class CacheEndpoint {

    @Value("${server.port}")
    private int port;

    private CacheService<String, byte[]> cacheService;

    @Autowired
    public CacheEndpoint(CacheService<String, byte[]> cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/hello")
    public Mono<String> sayHello(@RequestParam(value = "name", defaultValue = "fucker") String name) {
        return Mono.just("hello," + name + "!" + " FROM: " + port);
    }

    @GetMapping(value = "/{cacheName}/{key}")
    public Mono<byte[]> get(@PathVariable String cacheName, @PathVariable String key) {
        return Mono.create(emitter -> {
            byte[] cachedValue = cacheService.get(cacheName, key).orElse(null);
            if (cachedValue == null) {
                emitter.success();
            } else {
                emitter.success(cachedValue);
            }
        });
    }

    /**
     * This method will cause cache overwritten
     */
    @PostMapping(value = "/{cacheName}/{key}")
    public Mono<byte[]> put(@PathVariable String cacheName, @PathVariable String key,
                            @RequestBody Mono<byte[]> valuePublisher) {
        return Mono.create(emitter -> valuePublisher.subscribe(value ->
                cacheService.put(cacheName, key, value).ifPresent(emitter::success)));
    }

    @DeleteMapping(value = "/{cacheName}/{key}")
    public Mono<byte[]> remove(@PathVariable String cacheName, @PathVariable String key) {
        return Mono.create(emitter -> {
            byte[] cachedValue = cacheService.remove(cacheName, key).orElse(null);
            if (cachedValue == null) {
                emitter.success();
            } else {
                emitter.success(cachedValue);
            }
        });
    }

    @GetMapping(value = "/props")
    public Mono<CacheManagerProperties> getProperties() {
        return Mono.just(cacheService.getManagerProperties());
    }

    @GetMapping(value = "/props/{cacheName}")
    public Mono<CacheProperties> getCacheProperties(@PathVariable String cacheName) {
        return Mono.just(cacheService.getCacheProperties(cacheName));
    }

    @GetMapping(value = "/isClosed")
    public Mono<Boolean> isClosed() {
        return Mono.just(cacheService.isClosed());
    }

    @GetMapping(value = "/isClosed/{cacheName}")
    public Mono<Boolean> isClosed(@PathVariable String cacheName) {
        return Mono.just(cacheService.isClosed(cacheName));
    }
}
