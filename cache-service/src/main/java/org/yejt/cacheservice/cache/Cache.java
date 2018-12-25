package org.yejt.cacheservice.cache;

import org.yejt.cacheservice.properties.CacheProperties;
import org.yejt.cacheservice.store.DataStore;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;

/**
 * An cache abstract. It contains a name, and
 * contains a storage containing K-V pairs.
 *
 * @param <K>: Key type
 * @param <V>: Value type
 * @author keys961
 */
public interface Cache<K, V> extends Closeable {
    /**
     * Get the cached value from the given key
     *
     * @param key: Given key
     * @return Cached value of the key
     */
    V get(K key);

    /**
     * <p>Get all the cached value from a set of keys.
     * <p>It will return a K-V map with the key and the value.
     *
     * @param keys: Given set of keys
     * @return Cached K-V map
     */
    Map<K, V> getAll(Set<? extends K> keys);

    /**
     * Get the whole data storage of the cache.
     * @return The whole data storage of the cache
     */
    DataStore<K, V> getDataStore();

    /**
     * Put a new K-V pair into the cache storage.
     * @param key: Given key
     * @param value: Given value
     * @return The cached value
     */
    V put(K key, V value);

    /**
     * Delete a K-V pair with given key.
     * @param key: Given key
     * @return The deleted value
     */
    V remove(K key);

    /**
     * Test whether there contains the K-V pair with given key
     * @param key: Given key
     * @return Whether the K-V pair is in the cache storage
     */
    boolean containsKey(K key);

    /**
     * Clear the cache storage.
     */
    void clear();

    /**
     * Close the cache, with the storage cleaned.
     */
    @Override
    void close();

    /**
     * Test whether the cache is closed.
     * @return Whether the cache is closed
     */
    boolean isClosed();

    /**
     * Get the cache name.
     * @return Cache name
     */
    String getCacheName();

    /**
     * Get its cache properties.
     * @return Its cache properties
     */
    CacheProperties getProperties();

    /**
     * Cast the cache itself to the specified type of cache.
     * @param cls: Specified cache class
     * @param <T>: Specified cache type
     * @return Casted cache
     */
    <T> T unwrap(Class<T> cls);
}
