package org.yejt.cacheservice.store;

import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.Map;
import java.util.Set;

/**
 * The abstract interface of data storage containing K-V pairs
 *
 * @param <K>: Key type
 * @param <V>: Value type
 * @author keys961
 */
public interface DataStore<K, V> {
    /**
     * Get the value from the given key.
     *
     * @param key: Given key
     * @return Value in the holder
     */
    ValueHolder<V> get(K key);

    /**
     * Put the K-V pair to the storage.
     * @param key: Given key
     * @param value: Given value
     * @return Value stored in the holder
     */
    ValueHolder<V> put(K key, V value);

    /**
     * Remove the K-V pair from the storage.
     * @param key: Given key
     * @return Removed value in the holder
     */
    ValueHolder<V> remove(K key);

    /**
     * Get all K-V pairs in the holder.
     * @return All the K-V pairs in the storage in the form of K-V map
     */
    Set<Map.Entry<K, ValueHolder<V>>> getAll();

    /**
     * Clear all the K-V pairs in the storage.
     */
    void clear();
}
