package org.yejt.cacheservice.cache;

import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import java.io.Closeable;

public interface CacheManager<K, V> extends Closeable {
    Cache<K, V> getCache(String cacheName);

    Iterable<String> getCacheNames();

    Cache<K, V> createCache(String cacheName, CacheProperties cacheProperties);

    void destroyCache(String cacheName);

    CacheManagerProperties getProperties();

    void close();

    boolean isClosed();

}
