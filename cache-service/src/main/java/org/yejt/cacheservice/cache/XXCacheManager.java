package org.yejt.cacheservice.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import java.util.Collections;
import java.util.Map;

public class XXCacheManager implements CacheManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheManager.class);

    private Map<String, Cache<?, ?>> cacheMap;

    private CacheManagerProperties properties;

    private volatile boolean isClosed;

    public XXCacheManager(CacheManagerProperties properties)
    {
        this.properties = properties;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String cacheName)
    {
        return (Cache<K, V>) cacheMap.getOrDefault(cacheName, null);
    }

    @Override
    public Iterable<String> getCacheNames()
    {
        return Collections.unmodifiableSet(cacheMap.keySet());
    }

    @Override
    public <K, V> Cache<K, V> createCache(String cacheName, CacheProperties cacheProperties)
    {
        return null;
    }

    @Override
    public void destroyCache(String cacheName)
    {

    }

    @Override
    public CacheManagerProperties getProperties()
    {
        return properties;
    }

    @Override
    public void close()
    {

    }

    @Override
    public boolean isClosed()
    {
        return isClosed;
    }
}
