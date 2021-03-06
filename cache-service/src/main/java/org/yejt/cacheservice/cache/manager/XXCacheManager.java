package org.yejt.cacheservice.cache.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheservice.cache.Cache;
import org.yejt.cacheservice.cache.CacheManager;
import org.yejt.cacheservice.cache.cache.XXCache;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author keys961
 */
public class XXCacheManager implements CacheManager<String, byte[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheManager.class);

    private Map<String, Cache<String, byte[]>> cacheMap;

    private CacheManagerProperties properties;

    private volatile boolean isClosed;

    public XXCacheManager(CacheManagerProperties properties) {
        this.properties = properties;
        cacheMap = new ConcurrentHashMap<>();
        properties.getCaches()
                .forEach(p -> cacheMap.put(p.getCacheName(), new XXCache(p)));
    }

    @Override
    public Cache<String, byte[]> getCache(String cacheName) {
        return cacheMap.getOrDefault(cacheName, null);
    }

    @Override
    public Iterable<String> getCacheNames() {
        return Collections.unmodifiableSet(cacheMap.keySet());
    }

    @Override
    public Cache<String, byte[]> createCache(String cacheName, CacheProperties cacheProperties) {
        Cache<String, byte[]> cache = new XXCache(cacheProperties);
        cacheMap.put(cacheName, cache);
        LOGGER.info("XXCacheManager#createCache: Cache {} is created.", cacheName);
        return cache;
    }

    @Override
    public void destroyCache(String cacheName) {
        Cache<String, byte[]> cache = cacheMap.get(cacheName);
        if (cache != null) {
            cache.close();
            LOGGER.info("XXCacheManager#destroyCache: Cache {} is destroyed.", cacheName);
            cacheMap.remove(cacheName);
        }
    }

    @Override
    public CacheManagerProperties getProperties() {
        return properties;
    }

    @Override
    public void close() {
        isClosed = true;
        cacheMap.forEach((name, cache) -> cache.close());
        LOGGER.warn("XXCacheManager#close: CacheManager is destroyed.");
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }
}
