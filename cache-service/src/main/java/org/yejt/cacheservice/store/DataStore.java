package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.ValueHolder;

public interface DataStore<K, V>
{
    ValueHolder<V> get(K key);

    ValueHolder<V> put(K key, V value);

    ValueHolder<V> remove(K key);

    void clear();
}
