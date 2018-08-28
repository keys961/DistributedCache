package org.yejt.cacheclient.client;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yejt.cacheclient.client.fallback.XXCacheClientFallback;

@FeignClient(value = "${xxcache.route-name}", fallback = XXCacheClientFallback.class,
        path = "${xxcache.service-prefix}")
public interface XXCacheClient
{
    @GetMapping(value = "/{cacheName}/{key}")
    Object get(@PathVariable("cacheName") String cacheName,
                       @RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
                       @PathVariable("key") String key);

    /**
     * This method will cause cache overwritten
     */
    @PostMapping(value = "/{cacheName}/{key}")
    Object put(@PathVariable("cacheName") String cacheName,
             @RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
             @PathVariable("key") String key,
             @RequestBody Object value);

    @DeleteMapping(value = "/{cacheName}/{key}")
    Object remove(@PathVariable("cacheName") String cacheName,
                @RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
                @PathVariable("key") String key);

    @GetMapping("/hello")
    String sayHello(@RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
                    @RequestParam(value = "name") String name);
}
