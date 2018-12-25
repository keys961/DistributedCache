package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.BaseValueHolder;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>Bounded storage with strict LRU replacement strategy.
 * <p>Replacing strategy: LRU
 *
 * @author keys961
 */
public class LruDataStore<K, V> implements DataStore<K, V> {

    private LinkedHashMap<K, ValueHolder<V>> cache;

    private ReentrantLock lock = new ReentrantLock();

    public LruDataStore(long capacity) {
        final int mapCapacity = (int) ((capacity << 2) / 3);
        cache = new LinkedHashMap<K, ValueHolder<V>>(mapCapacity) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, ValueHolder<V>> eldest) {
                return size() >= capacity;
            }
        };
    }

    @Override
    public ValueHolder<V> get(K key) {
        ValueHolder<V> v;
        try {
            lock.lock();
            v = cache.get(key);
            if (v != null) {
                cache.remove(key);
                cache.put(key, v);
            }
        } finally {
            lock.unlock();
        }

        return v;
    }

    @Override
    public Set<Map.Entry<K, ValueHolder<V>>> getAll() {
        Set<Map.Entry<K, ValueHolder<V>>> entries;
        try {
            lock.lock();
            entries = cache.entrySet();
        } finally {
            lock.unlock();
        }

        return entries;
    }

    @Override
    public ValueHolder<V> put(K key, V value) {
        ValueHolder<V> holder;
        try {
            lock.lock();
            holder = new BaseValueHolder<>(value);
            cache.put(key, holder);
        } finally {
            lock.unlock();
        }

        return holder;
    }

    @Override
    public ValueHolder<V> remove(K key) {
        ValueHolder<V> v;
        try {
            lock.lock();
            v = cache.remove(key);
        } finally {
            lock.unlock();
        }

        return v;
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            cache.clear();
        } finally {
            lock.unlock();
        }
    }
}
