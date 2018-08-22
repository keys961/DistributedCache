package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.BaseValueHolder;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Replacing strategy: FIFO
 */
public class FifoDataStore<K, V> implements DataStore<K, V>
{
    private final long capacity;

    private long count = 0L;

    private LinkedHashMap<K, ValueHolder<V>> cache;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public FifoDataStore(long capacity)
    {
        this.capacity = capacity;
        cache = new LinkedHashMap<>();
    }

    @Override
    public ValueHolder<V> get(K key)
    {
        lock.readLock().lock();
        ValueHolder<V> v = cache.get(key);
        lock.readLock().unlock();

        return v;
    }

    @Override
    public ValueHolder<V> put(K key, V value)
    {
        lock.writeLock().lock();
        if(count >= capacity)
            removeEntry();
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
        ValueHolder<V> holder;
        lock.writeLock().lock();

        holder = cache.remove(key);

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

    private void removeEntry()
    {
        // double check
        if(count < capacity || cache.isEmpty())
            return;

        K key = findFirstEntry();
        cache.remove(key);
    }

    private K findFirstEntry()
    {
        return cache.keySet().iterator().next();
    }
}
