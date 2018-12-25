package org.yejt.cacheservice.cache;

import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import java.io.Closeable;

/**
 * An cache manager interface that manages number of {@link Cache}s.
 *
 * @param <K>: Key type
 * @param <V>: Value type
 * @author keys961
 */
public interface CacheManager<K, V> extends Closeable {
    /**
     * Get the specified cache.
     *
     * @param cacheName: Specified cache name
     * @return Specified cache
     */
    Cache<K, V> getCache(String cacheName);

    /**
     * Get all the cache names of the caches in it
     * @return All the cache names
     */
    Iterable<String> getCacheNames();

    /**
     * Create cache with specified name and properties
     * @param cacheName: Cache name
     * @param cacheProperties: Given cache properties
     * @return Created cache
     */
    Cache<K, V> createCache(String cacheName, CacheProperties cacheProperties);

    /**
     * Destroy a cache with given cache name.
     * @param cacheName: Cache name
     */
    void destroyCache(String cacheName);

    /**
     * Get cache management properties
     * @return Cache management properties
     */
    CacheManagerProperties getProperties();

    /**
     * Close the cache management module, it will close all the caches it has.
     */
    @Override
    void close();

    /**
     * Test whether the cache manager is closed.
     * @return Whether the cache manager is closed
     */
    boolean isClosed();

}
