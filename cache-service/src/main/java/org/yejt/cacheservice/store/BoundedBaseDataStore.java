package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.BaseValueHolder;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Bounded base data store
 * Replacing Strategy: Random Pick
 */
public class BoundedBaseDataStore<K, V> implements DataStore<K, V>
{
    private final long capacity;

    private long count = 0L;

    private Map<K, ValueHolder<V>> cache;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public BoundedBaseDataStore(long capacity)
    {
        this.capacity = capacity;
        this.cache = new HashMap<>(Long.valueOf(capacity).intValue());
    }

    @Override
    public ValueHolder<V> get(K key)
    {
        ValueHolder<V> v;
        lock.readLock().lock();
        v = cache.get(key);
        lock.readLock().unlock();

        return v;
    }

    @Override
    public ValueHolder<V> put(K key, V value)
    {
        lock.writeLock().lock();
        if(count >= capacity)
            removeRandomly();
        ValueHolder<V> oldValue, newValue = new BaseValueHolder<>(value);
        oldValue = cache.get(key);
        cache.put(key, newValue);
        if(oldValue == null)
            count++;
        lock.writeLock().unlock();
        return newValue;
    }

    @Override
    public ValueHolder<V> remove(K key)
    {
        lock.writeLock().lock();
        ValueHolder<V> holder = cache.remove(key);

        if(holder != null)
            count--;
        lock.writeLock().unlock();
        return holder;
    }

    @Override
    public void clear()
    {
        lock.writeLock().lock();
        cache.clear();
        count = 0L;
        lock.writeLock().unlock();
    }

    private void removeRandomly()
    {
        // double check
        if(count < capacity)
            return;

        Optional<K> optionalKey = cache.keySet().stream().parallel().findAny();
        optionalKey.ifPresent(key -> cache.remove(key));
    }

}
