package org.yejt.cacheservice.service;

import org.yejt.cacheservice.cache.Cache;
import org.yejt.cacheservice.cache.CacheManager;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import java.util.Optional;

/**
 * Cache service interface.
 *
 * @param <K>: Key type
 * @param <V>: Value type
 * @author keys961
 */
public interface CacheService<K, V> {
    /**
     * Get specified cache by the given cache name
     *
     * @param cacheName: Given cache name
     * @return Specified cache
     */
    Cache<K, V> getCache(String cacheName);

    /**
     * Get the cache manager
     * @return Cache manager
     */
    CacheManager<K, V> getCacheManager();

    /**
     * Put the K-V pair into the given cache.
     * @param cacheName: Cache name
     * @param key: Given key
     * @param value: Given value
     * @return The cached value
     */
    Optional<V> put(String cacheName, K key, V value);

    /**
     * Get the cached value in the specified cache by the given key.
     * @param cacheName: Cache name
     * @param key: Given key
     * @return The cached value
     */
    Optional<V> get(String cacheName, K key);

    /**
     * Remove the K-V pair from the specified cache.
     * @param cacheName: Cache name
     * @param key: Given key
     * @return Removed cache value
     */
    Optional<V> remove(String cacheName, K key);

    /**
     * Get the cache manager properties.
     * @return The cache manager properties
     */
    CacheManagerProperties getManagerProperties();

    /**
     * Get the cache properties by the given cache name.
     * @param cacheName: Cache name
     * @return The related cache properties
     */
    CacheProperties getCacheProperties(String cacheName);

    /**
     * Close the whole cache service.
     */
    void close();

    /**
     * Close the cache by the given cache name.
     * @param cacheName: Cache name
     */
    void close(String cacheName);

    /**
     * Test whether the cache service is closed.
     * @return Whether the cache service is closed
     */
    boolean isClosed();

    /**
     * Test whether the specified cache is closed.
     * @param cacheName: Cache name
     * @return Whether the cache is closed
     */
    boolean isClosed(String cacheName);
}
