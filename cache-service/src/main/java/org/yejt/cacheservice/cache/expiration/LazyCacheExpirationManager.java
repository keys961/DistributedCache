package org.yejt.cacheservice.cache.expiration;

import org.yejt.cacheservice.cache.CacheExpirationManager;
import org.yejt.cacheservice.constant.CacheExpirationConstants;
import org.yejt.cacheservice.properties.CacheExpirationProperties;
import org.yejt.cacheservice.store.DataStore;
import org.yejt.cacheservice.store.value.ValueHolder;

/**
 * <p>Expiration manager using lazy strategy.
 * <p>Just check the expiration when a request comes.
 *
 * @param <K>: Key type
 * @param <V>: Value type
 * @author keys961
 */
public class LazyCacheExpirationManager<K, V> implements CacheExpirationManager<K, V> {
    private DataStore<K, V> dataStore;

    private CacheExpirationProperties properties;

    public LazyCacheExpirationManager(DataStore<K, V> dataStore, CacheExpirationProperties properties) {
        this.dataStore = dataStore;
        this.properties = properties;
        if (properties.getExpiration() <= 0) {
            properties.setExpiration(CacheExpirationConstants.DEFAULT_EXPIRATION);
        }
    }

    @Override
    public boolean isExpired(ValueHolder<V> v) {
        long currentTime = System.currentTimeMillis();
        return v.getTimestamp() < currentTime - properties.getExpiration() * 1000;
    }

    @Override
    public void remove(K key) {
        dataStore.remove(key);
    }
}
