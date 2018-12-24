package org.yejt.cacheservice.service;

import org.yejt.cacheservice.cache.Cache;
import org.yejt.cacheservice.cache.CacheManager;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import java.util.Optional;

public interface CacheService
{
    Cache getCache(String cacheName);

    CacheManager getCacheManager();

    //TODO: deal with value as raw...
    Optional put(String cacheName, String key, Object value);

    Optional get(String cacheName, String key);

    Optional remove(String cacheName, String key);

    CacheManagerProperties getManagerProperties();

    CacheProperties getCacheProperties(String cacheName);

    void close();

    void close(String cacheName);

    boolean isClosed();

    boolean isClosed(String cacheName);
}
