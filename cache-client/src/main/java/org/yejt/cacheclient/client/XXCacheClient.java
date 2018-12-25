package org.yejt.cacheclient.client;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.yejt.cacheclient.client.fallback.XXCacheClientFallback;
import org.yejt.cacheclient.codec.CacheCodec;

/**
 * Client used for caching.
 *
 * @author keys961
 */
@FeignClient(value = "${xxcache.route-name}", fallback = XXCacheClientFallback.class,
        path = "${xxcache.service-prefix}")
public interface XXCacheClient {

    /**
     * <p>Get the cache value from given <code>cacheName</code> and
     * <code>key</code>.</p>
     *
     * @param cacheName: Cache name
     * @param key:       Cache key
     * @return Cached value in <code>byte[]</code>
     */
    @GetMapping(value = "/{cacheName}/{key}")
    byte[] get(@PathVariable("cacheName") String cacheName,
               @RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
               @PathVariable("key") String key);

    /**
     * <p>Put the cache value from given <code>cacheName</code>
     *  and <code>key</code>.</p>
     * <p>This method will cause cache overwritten.</p>
     *
     * @param cacheName: Cache name
     * @param key: Cache key
     * @param value: Cache value in <code>byte[]</code>, this is determined by
     *             {@link CacheCodec}
     * @return Cached value in <code>byte[]</code>
     */
    @PostMapping(value = "/{cacheName}/{key}")
    byte[] put(@PathVariable("cacheName") String cacheName,
               @RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
               @PathVariable("key") String key,
               @RequestBody byte[] value);

    /**
     * <p>Put the cache value from given <code>cacheName</code>
     * and <code>key</code>.</p>
     *
     * @param cacheName: Cache name
     * @param key: Cache key
     * @return Deleted cached value in <code>byte[]</code>
     */
    @DeleteMapping(value = "/{cacheName}/{key}")
    byte[] remove(@PathVariable("cacheName") String cacheName,
                  @RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
                  @PathVariable("key") String key);

    /**
     * <p>Just a ping API of the client</p>
     * @param name: Name
     * @return Hello response
     */
    @GetMapping("/hello")
    String sayHello(@RequestHeader(FilterConstants.LOAD_BALANCER_KEY)
                    @RequestParam(value = "name") String name);
}
