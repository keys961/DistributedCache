package org.yejt.cacheclient.client;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.yejt.cacheclient.client.fallback.XXCacheClientFallback;

@FeignClient(value = "${xxcache.route-name}", fallback = XXCacheClientFallback.class,
        path = "${xxcache.service-prefix}")
public interface XXCacheClient
{
    @GetMapping(value = "/{cacheName}/{key}")
    byte[] get(@PathVariable("cacheName") String cacheName,
               @RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
                       @PathVariable("key") String key);

    /**
     * This method will cause cache overwritten
     */
    @PostMapping(value = "/{cacheName}/{key}")
    byte[] put(@PathVariable("cacheName") String cacheName,
               @RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
             @PathVariable("key") String key,
               @RequestBody byte[] value);

    @DeleteMapping(value = "/{cacheName}/{key}")
    byte[] remove(@PathVariable("cacheName") String cacheName,
                  @RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
                @PathVariable("key") String key);

    @GetMapping("/hello")
    byte[] sayHello(@RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
                    @RequestParam(value = "name") String name);
}
