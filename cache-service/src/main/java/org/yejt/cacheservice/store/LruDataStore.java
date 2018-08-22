package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.BaseValueHolder;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Strict LRU Cache store
 * Replacing strategy: LRU
 */
public class LruDataStore<K, V> implements DataStore<K, V>
{
    private final long capacity;

    private LinkedHashMap<K, ValueHolder<V>> cache;

    private ReentrantLock lock = new ReentrantLock();

    public LruDataStore(long capacity)
    {
        this.capacity = capacity;
        final int mapCapacity = (int)((capacity << 2) / 3);
        cache = new LinkedHashMap<K, ValueHolder<V>>(mapCapacity)
        {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, ValueHolder<V>> eldest)
            {
                return size() >= mapCapacity;
            }
        };
    }

    @Override
    public ValueHolder<V> get(K key)
    {
        lock.lock();
        ValueHolder<V> v = cache.get(key);
        if(v != null)
        {
            cache.remove(key);
            cache.put(key, v);
        }
        lock.unlock();

        return v;
    }

    @Override
    public ValueHolder<V> put(K key, V value)
    {
        lock.lock();
        ValueHolder<V> holder = new BaseValueHolder<>(value);
        cache.put(key, holder);
        lock.unlock();

        return holder;
    }

    @Override
    public ValueHolder<V> remove(K key)
    {
        lock.lock();
        ValueHolder<V> v = cache.remove(key);
        lock.unlock();

        return v;
    }

    @Override
    public void clear()
    {
        lock.lock();
        cache.clear();
        lock.unlock();
    }
}
