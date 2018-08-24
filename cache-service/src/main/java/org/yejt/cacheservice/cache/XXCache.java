package org.yejt.cacheservice.cache;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheservice.properties.CacheExpirationProperties;
import org.yejt.cacheservice.properties.CacheProperties;
import org.yejt.cacheservice.store.DataStore;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class XXCache<K, V> implements Cache<K, V>
{
    private final Logger LOGGER = LoggerFactory.getLogger(XXCache.class);

    private DataStore<K, V> dataStore;

    private CacheProperties properties;

    private CacheExpirationManager<K> cacheExpirationManager;

    private volatile boolean isClosed;

    public XXCache(CacheProperties properties)
    {
        this.properties = properties;
        buildCache();
        LOGGER.info("XXCache - Cache {} is built.", properties.getCacheName());
    }

    @Override
    public V get(K key)
    {
        if(isClosed)
            throw new RuntimeException("Cache is closed.");
        K filteredKey = cacheExpirationManager.filter(key);
        if(filteredKey == null)
        {
            LOGGER.info("XXCache - {}#get: key: {} is expired.", properties.getCacheName(),
                    key);
            return null;
        }
        ValueHolder<V> v = dataStore.get(filteredKey);
        LOGGER.info("XXCache - {}#get: key: {}, value: {}.", properties.getCacheName(),
                filteredKey, v);
        if(v == null)
            return null;

        return v.value();
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
        cacheExpirationManager.updateTimestamp(key);
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
        cacheExpirationManager.removeTimestamp(key);
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
        cacheExpirationManager.clearTimestamp();
    }

    @Override
    public void close()
    {
        if(isClosed)
            return;
        isClosed = true;
        dataStore.clear();
        cacheExpirationManager.clearTimestamp();
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

        throw new IllegalArgumentException("Unwapping to " + cls
                + " is not a supported by this implementation");
    }

    protected void buildCache()
    {
        // TODO: build cache..
    }
}
