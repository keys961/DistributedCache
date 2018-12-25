package org.yejt.cacheservice.service;

import org.yejt.cacheservice.cache.Cache;
import org.yejt.cacheservice.cache.CacheManager;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import java.util.Optional;

public interface CacheService<K, V> {
    Cache<K, V> getCache(String cacheName);

    CacheManager<K, V> getCacheManager();

    Optional<V> put(String cacheName, K key, V value);

    Optional<V> get(String cacheName, K key);

    Optional<V> remove(String cacheName, K key);

    CacheManagerProperties getManagerProperties();

    CacheProperties getCacheProperties(String cacheName);

    void close();

    void close(String cacheName);

    boolean isClosed();

    boolean isClosed(String cacheName);
}
