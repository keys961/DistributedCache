package org.yejt.cacheservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yejt.cacheservice.cache.Cache;
import org.yejt.cacheservice.cache.CacheManager;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class XXCacheService implements CacheService
{
    private CacheManager cacheManager;

    @Autowired
    public XXCacheService(CacheManager cacheManager)
    {
        this.cacheManager = cacheManager;
    }

    @Override
    public Cache getCache(String cacheName)
    {
        return cacheManager.getCache(cacheName);
    }

    @Override
    public CacheManager getCacheManager()
    {
        return cacheManager;
    }

    @Override
    public Optional put(String cacheName, String key, Object value)
    {
        return Optional.ofNullable(cacheManager.getCache(cacheName).
                put(key, value));
    }

    @Override
    public Optional get(String cacheName, String key)
    {
        return Optional.ofNullable(cacheManager.getCache(cacheName).get(key));
    }

    @Override
    public Optional remove(String cacheName, String key)
    {
        return Optional.ofNullable(cacheManager.getCache(cacheName).remove(key));
    }

    @Override
    public CacheManagerProperties getManagerProperties()
    {
        return cacheManager.getProperties();
    }

    @Override
    public CacheProperties getCacheProperties(String cacheName)
    {
        Cache cache = cacheManager.getCache(cacheName);
        if(cache != null)
            return cache.getProperties();
        return null;
    }

    @Override
    public void close()
    {
        cacheManager.close();
    }

    @Override
    public void close(String cacheName)
    {
        cacheManager.destroyCache(cacheName);
    }

    @Override
    public boolean isClosed()
    {
        return cacheManager.isClosed();
    }

    @Override
    public boolean isClosed(String cacheName)
    {
        return cacheManager.getCache(cacheName).isClosed();
    }
}
