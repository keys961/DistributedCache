package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.BaseValueHolder;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>Bounded base data store.
 * <p>Replacing strategy: random pick.
 *
 * @author keys961
 */
public class BoundedBaseDataStore<K, V> implements DataStore<K, V> {
    private final long capacity;

    private long count = 0L;

    private Map<K, ValueHolder<V>> cache;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public BoundedBaseDataStore(long capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>(Long.valueOf(capacity).intValue());
    }

    @Override
    public Set<Map.Entry<K, ValueHolder<V>>> getAll() {
        Set<Map.Entry<K, ValueHolder<V>>> entries;
        try {
            lock.readLock().lock();
            entries = cache.entrySet();
        } finally {
            lock.readLock().unlock();
        }

        return entries;
    }

    @Override
    public ValueHolder<V> get(K key) {
        ValueHolder<V> v;
        try {
            lock.readLock().lock();
            v = cache.get(key);
        } finally {
            lock.readLock().unlock();
        }

        return v;
    }

    @Override
    public ValueHolder<V> put(K key, V value) {
        ValueHolder<V> oldValue, newValue;
        try {
            lock.writeLock().lock();
            if (count >= capacity) {
                removeRandomly();
            }
            newValue = new BaseValueHolder<>(value);
            oldValue = cache.get(key);
            cache.put(key, newValue);
            if (oldValue == null) {
                count++;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return newValue;
    }

    @Override
    public ValueHolder<V> remove(K key) {
        ValueHolder<V> holder = null;
        try {
            lock.writeLock().lock();
            holder = cache.remove(key);
            if (holder != null) {
                count--;
            }
        } finally {
            lock.writeLock().unlock();
        }

        return holder;
    }

    @Override
    public void clear() {
        try {
            lock.writeLock().lock();
            cache.clear();
            count = 0L;
        } finally {
            lock.writeLock().unlock();
        }

    }

    private void removeRandomly() {
        // double check
        if (count < capacity) {
            return;
        }

        Optional<K> optionalKey = cache.keySet().stream().parallel().findAny();
        optionalKey.ifPresent(key -> cache.remove(key));
    }

}
