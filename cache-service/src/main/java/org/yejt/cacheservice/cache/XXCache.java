package org.yejt.cacheservice.cache;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private volatile boolean isClosed;

    public XXCache(CacheProperties properties)
    {
        this.properties = properties;
    }

    @Override
    public V get(K key)
    {
        if(isClosed)
            throw new RuntimeException("Cache is closed.");
        ValueHolder<V> v = dataStore.get(key);
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

        return kvMap;
    }

    @Override
    public V put(K key, V value)
    {
        if(isClosed)
            throw new RuntimeException("Cache is closed.");
        ValueHolder<V> v = dataStore.put(key, value);
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
        dataStore.clear();
    }

    @Override
    public void close()
    {
        if(isClosed)
            return;
        isClosed = true;
        dataStore.clear();
        dataStore = null;
    }

    @Override
    public boolean isClosed()
    {
        return false;
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
        return null;
    }
}
