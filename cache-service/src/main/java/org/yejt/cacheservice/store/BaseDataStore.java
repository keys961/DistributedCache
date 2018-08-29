package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.BaseValueHolder;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unbounded base data store, default usage..
 */
public class BaseDataStore<K, V> implements DataStore<K, V>
{
    private ConcurrentHashMap<K, ValueHolder<V>> cache = new ConcurrentHashMap<>();

    @Override
    public ValueHolder<V> get(K key)
    {
        return cache.get(key);
    }

    @Override
    public Set<Map.Entry<K, ValueHolder<V>>> getAll()
    {
        return cache.entrySet();
    }

    @Override
    public ValueHolder<V> put(K key, V value)
    {
        ValueHolder<V> v = new BaseValueHolder<>(value);
        cache.put(key, v);

        return v;
    }

    @Override
    public ValueHolder<V> remove(K key)
    {
        return cache.remove(key);
    }

    @Override
    public void clear()
    {
        cache.clear();
    }
}
