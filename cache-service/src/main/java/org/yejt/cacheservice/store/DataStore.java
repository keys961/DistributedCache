package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.Map;
import java.util.Set;

public interface DataStore<K, V> {
    ValueHolder<V> get(K key);

    ValueHolder<V> put(K key, V value);

    ValueHolder<V> remove(K key);

    Set<Map.Entry<K, ValueHolder<V>>> getAll();

    void clear();
}
