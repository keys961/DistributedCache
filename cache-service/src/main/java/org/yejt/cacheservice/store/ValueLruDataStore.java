package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.Map;
import java.util.Set;

public class ValueLruDataStore<K, V> implements DataStore<K, V>
{
    @Override
    public ValueHolder<V> get(K key)
    {
        return null;
    }

    @Override
    public ValueHolder<V> put(K key, V value)
    {
        return null;
    }

    @Override
    public ValueHolder<V> remove(K key)
    {
        return null;
    }

    @Override
    public Set<Map.Entry<K, ValueHolder<V>>> getAll()
    {
        return null;
    }

    @Override
    public void clear() {

    }
}
