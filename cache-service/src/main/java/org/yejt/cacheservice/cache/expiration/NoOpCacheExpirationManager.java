package org.yejt.cacheservice.cache.expiration;

import org.yejt.cacheservice.cache.CacheExpirationManager;
import org.yejt.cacheservice.store.value.ValueHolder;

/**
 * No-op expiration manager, it won't check any expiration.
 *
 * @param <K>: Key type
 * @param <V>: Value type
 * @author keys961
 */
public class NoOpCacheExpirationManager<K, V> implements CacheExpirationManager<K, V> {
    @Override
    public boolean isExpired(ValueHolder<V> v) {
        return false;
    }

    @Override
    public void remove(Object key) {
        // no-ops
    }
}
