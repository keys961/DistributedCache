package org.yejt.cacheservice.cache;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheservice.constant.CacheExpirationConstants;
import org.yejt.cacheservice.constant.CacheTypeConstants;
import org.yejt.cacheservice.properties.CacheExpirationProperties;
import org.yejt.cacheservice.properties.CacheProperties;
import org.yejt.cacheservice.store.*;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class XXCache<K, V> implements Cache<K, V>
{
    private final Logger LOGGER = LoggerFactory.getLogger(XXCache.class);

    private DataStore<K, V> dataStore;

    private CacheProperties properties;

    private CacheExpirationManager<K, V> cacheExpirationManager;

    private volatile boolean isClosed;

    // TODO: Optimize the cache speed..
    public XXCache(CacheProperties properties)
    {
        this.properties = properties;
        buildCache();
        LOGGER.info("XXCache - Cache {} is built.", properties.getCacheName());
    }

    private void buildCache()
    {
        buildDataStore();
        buildExpirationManager();
    }

    private void buildDataStore()
    {
        if(properties.getCacheType() == null)
        {
            dataStore = new BaseDataStore<>();
            return;
        }

        switch (properties.getCacheType())
        {
            case CacheTypeConstants
                    .BASIC:
            case CacheTypeConstants.DEFAULT:
                dataStore = new BaseDataStore<>(); break;
            case CacheTypeConstants.BOUNDED_BASIC:
                dataStore = new BoundedBaseDataStore<>(properties.getMaxSize());
                break;
            case CacheTypeConstants.FIFO:
                dataStore = new FifoDataStore<>(properties.getMaxSize());
                break;
            case CacheTypeConstants.LRU:
                dataStore = new LruDataStore<>(properties.getMaxSize());
                break;
            case CacheTypeConstants.APPROXIMATE_LRU:
                dataStore = new ApproximateLruDataStore<>(properties.getMaxSize());
                break;
            case CacheTypeConstants.WEAK_REFERENCE:
                dataStore = new WeakReferenceDataStore<>();
                break;
            default:
                dataStore = new BaseDataStore<>();
        }
    }

    private void buildExpirationManager()
    {
        CacheExpirationProperties expirationProperties = properties.getExpiration();
        if(expirationProperties == null || expirationProperties.getStrategy() == null)
        {
            cacheExpirationManager = new NoOpCacheExpirationManager<>();
            return;
        }

        switch (expirationProperties.getStrategy())
        {
            case CacheExpirationConstants
                    .NOOP_STRATEGY:
                cacheExpirationManager = new NoOpCacheExpirationManager<>();
                break;
            case CacheExpirationConstants.LAZY_STRATEGY:
                cacheExpirationManager = new LazyCacheExpirationManager<>(dataStore, expirationProperties);
                break;
            default:
                cacheExpirationManager = new NoOpCacheExpirationManager<>();
        }
    }


    @Override
    public V get(K key)
    {
        if(isClosed)
            throw new RuntimeException("Cache is closed.");

        V v = cacheExpirationManager.filter(key, dataStore.get(key));
        LOGGER.info("XXCache - {}#get: key: {}, value: {}.", properties.getCacheName(),
                key, v);

        return v;
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys)
    {
        if(isClosed)
            throw new RuntimeException("Cache is closed.");
        Map<K, V> kvMap = new HashMap<>();
        keys.forEach(k -> kvMap.put(k, get(k)));
        LOGGER.info("XXCache - {}#getAll: keys: {}, values: {}.", properties.getCacheName(),
                keys, kvMap);
        return kvMap;
    }

    @Override
    public V put(K key, V value)
    {
        if(isClosed)
            throw new RuntimeException("Cache is closed.");
        ValueHolder<V> v = dataStore.put(key, value);
        LOGGER.info("XXCache - {}#put: key: {}, value: {}.", properties.getCacheName(),
                key, v);
        if(v == null)
            return null;

        return v.value();
    }

    @Override
    public V remove(K key)
    {
        if(isClosed)
            throw new RuntimeException("Cache is closed.");
        ValueHolder<V> v = dataStore.remove(key);
        LOGGER.info("XXCache - {}#remove: key: {}, value: {}.", properties.getCacheName(),
                key, v);
        if(v == null)
            return null;

        return v.value();
    }

    @Override
    public boolean containsKey(K key)
    {
        if(isClosed)
            throw new RuntimeException("Cache is closed.");
        return get(key) != null;
    }

    @Override
    public void clear()
    {
        if(isClosed)
            throw new RuntimeException("Cache is closed.");
        LOGGER.warn("XXCache - {}#clear.");
        dataStore.clear();
    }

    @Override
    public void close()
    {
        if(isClosed)
            return;
        isClosed = true;
        dataStore.clear();
        LOGGER.warn("XXCache - {}#clear.");
    }

    @Override
    public boolean isClosed()
    {
        return isClosed;
    }

    @Override
    public String getCacheName()
    {
        return properties.getCacheName();
    }

    @Override
    public CacheProperties getProperties()
    {
        return properties;
    }

    @Override
    public <T> T unwrap(Class<T> cls)
    {
        if (cls.isAssignableFrom(getClass()))
        {
            return cls.cast(this);
        }

        throw new IllegalArgumentException("Unwrapping to " + cls
                + " is not a supported by this implementation");
    }


}
