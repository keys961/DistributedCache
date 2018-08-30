package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class ValueLruDataStore<K, V> implements DataStore<K, V>
{
    // max size, concurrency level (with avg size up to 2^n bytes)

    @Override
    public ValueHolder<V> get(K key) {
        return null;
    }

    @Override
    public ValueHolder<V> put(K key, V value) {
        return null;
    }

    @Override
    public ValueHolder<V> remove(K key) {
        return null;
    }

    @Override
    public Set<Map.Entry<K, ValueHolder<V>>> getAll() {
        return null;
    }

    @Override
    public void clear() {

    }

    class Segment extends ReentrantLock
    {

    }
}
