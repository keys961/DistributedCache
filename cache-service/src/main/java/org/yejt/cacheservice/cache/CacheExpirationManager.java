package org.yejt.cacheservice.cache;

import org.yejt.cacheservice.store.value.ValueHolder;

/**
 * <p>An expiration manager interface.
 * It can be extended with other expiration strategy.
 * <p>It implemented the template design pattern, thus you just
 * need to implement the {@link CacheExpirationManager#isExpired(ValueHolder)}
 * and {@link CacheExpirationManager#remove(Object)} methods, and apply the
 * strategy to the cache service, it will work when dealing with the expiration.</p>
 *
 * @param <K>: Key type
 * @param <V>: Value type
 * @author keys961
 */
public interface CacheExpirationManager<K, V> {
    /**
     * Test whether the value is expired.
     * @param v: Given value
     * @return Whether the value is expired
     */
    boolean isExpired(ValueHolder<V> v);

    /**
     * <p>Filter the K-V pair.
     * <p>If the K-V pair is expired, remove it and return <code>null</code>.
     *
     * @param key:         Given key
     * @param valueHolder: Given value
     * @return The filtered value
     */
    default V filter(K key, ValueHolder<V> valueHolder) {
        if (key == null || valueHolder == null) {
            return null;
        }
        if (!isExpired(valueHolder)) {
            return valueHolder.value();
        }
        // remove expired item
        remove(key);
        return null;
    }

    /**
     * Remove the expired key.
     * @param key: Given key
     */
    void remove(K key);
}
