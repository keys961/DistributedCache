package org.yejt.cacheservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yejt.cacheservice.cache.Cache;
import org.yejt.cacheservice.cache.CacheManager;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import java.util.Optional;

/**
 * XXCache service
 *
 * @author yejt
 */
@Service
public class XXCacheService implements CacheService<String, byte[]> {
    private CacheManager<String, byte[]> cacheManager;

    @Autowired
    public XXCacheService(CacheManager<String, byte[]> cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Cache<String, byte[]> getCache(String cacheName) {
        return cacheManager.getCache(cacheName);
    }

    @Override
    public CacheManager<String, byte[]> getCacheManager() {
        return cacheManager;
    }

    @Override
    public Optional<byte[]> put(String cacheName, String key, byte[] value) {
        Cache<String, byte[]> cache = getCache(cacheName);
        if (cache == null)
            return Optional.empty();
        return Optional.ofNullable(cacheManager.getCache(cacheName).
                put(key, value));
    }

    @Override
    public Optional<byte[]> get(String cacheName, String key) {
        Cache<String, byte[]> cache = getCache(cacheName);
        if (cache == null)
            return Optional.empty();
        return Optional.ofNullable(cacheManager.getCache(cacheName).get(key));
    }

    @Override
    public Optional<byte[]> remove(String cacheName, String key) {
        Cache<String, byte[]> cache = getCache(cacheName);
        if (cache == null)
            return Optional.empty();
        return Optional.ofNullable(cacheManager.getCache(cacheName).remove(key));
    }

    @Override
    public CacheManagerProperties getManagerProperties() {
        return cacheManager.getProperties();
    }

    @Override
    public CacheProperties getCacheProperties(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null)
            return cache.getProperties();
        return null;
    }

    @Override
    public void close() {
        cacheManager.close();
    }

    @Override
    public void close(String cacheName) {
        cacheManager.destroyCache(cacheName);
    }

    @Override
    public boolean isClosed() {
        return cacheManager.isClosed();
    }

    @Override
    public boolean isClosed(String cacheName) {
        return cacheManager.getCache(cacheName).isClosed();
    }
}
