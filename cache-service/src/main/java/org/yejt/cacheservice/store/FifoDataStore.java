package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.BaseValueHolder;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>Bounded storage using FIFO replacement.
 * <p>Replacing strategy: FIFO.
 *
 * @author keys961
 */
public class FifoDataStore<K, V> implements DataStore<K, V> {
    private final long capacity;

    private long count = 0L;

    private LinkedHashMap<K, ValueHolder<V>> cache;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public FifoDataStore(long capacity) {
        this.capacity = capacity;
        cache = new LinkedHashMap<>();
    }

    @Override
    public ValueHolder<V> get(K key) {
        ValueHolder<V> v = null;
        try {
            lock.readLock().lock();
            v = cache.get(key);
            lock.readLock().unlock();
        } finally {
            lock.readLock().unlock();
        }
        return v;
    }

    @Override
    public Set<Map.Entry<K, ValueHolder<V>>> getAll() {
        Set<Map.Entry<K, ValueHolder<V>>> entries = new HashSet<>();
        try {
            lock.readLock().lock();
            entries = cache.entrySet();
        } finally {
            lock.readLock().unlock();
        }
        return entries;
    }

    @Override
    public ValueHolder<V> put(K key, V value) {
        ValueHolder<V> oldValue, newValue = new BaseValueHolder<>(value);
        try {
            lock.writeLock().lock();
            if (count >= capacity) {
                removeEntry();
            }
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

    private void removeEntry() {
        // double check
        if (count < capacity || cache.isEmpty()) {
            return;
        }

        K key = findFirstEntry();
        cache.remove(key);
    }

    private K findFirstEntry() {
        return cache.keySet().iterator().next();
    }
}
