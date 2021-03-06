package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.container.ConcurrentLRUHashMap;
import org.yejt.cacheservice.store.value.BaseValueHolder;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.Map;
import java.util.Set;

/**
 * <p>Approximately LRU algorithms using segment to provide
 * better performance.
 * <p>Replacing strategy: LRU.
 *
 * @author keys961
 */
public class ApproximateLruDataStore<K, V> implements DataStore<K, V> {
    private ConcurrentLRUHashMap<K, ValueHolder<V>> cache;

    public ApproximateLruDataStore(long capacity) {
        cache = new ConcurrentLRUHashMap<>((int) capacity);
    }

    @Override
    public ValueHolder<V> get(K key) {
        return cache.get(key);
    }

    @Override
    public Set<Map.Entry<K, ValueHolder<V>>> getAll() {
        return cache.entrySet();
    }

    @Override
    public ValueHolder<V> put(K key, V value) {
        ValueHolder<V> v = new BaseValueHolder<>(value);
        cache.put(key, v);

        return v;
    }

    @Override
    public ValueHolder<V> remove(K key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
