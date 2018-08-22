package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.ValueHolder;
import org.yejt.cacheservice.store.value.WeakReferenceValueHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WeakReference cache store
 * No need to be bounded for it will be GC.
 */
public class WeakReferenceDataStore<K, V> implements DataStore<K, V>
{
    private Map<K, ValueHolder<V>> cacheMap = new ConcurrentHashMap<>();

    @Override
    public ValueHolder<V> get(K key)
    {
        return cacheMap.get(key);
    }

    @Override
    public ValueHolder<V> put(K key, V value)
    {
        ValueHolder<V> v = new WeakReferenceValueHolder<>(value);
        cacheMap.put(key, v);
        return v;
    }

    @Override
    public ValueHolder<V> remove(K key)
    {
        return cacheMap.get(key);
    }

    @Override
    public void clear()
    {
        cacheMap.clear();
    }
}
