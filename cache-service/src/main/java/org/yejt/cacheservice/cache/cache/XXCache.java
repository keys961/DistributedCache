package org.yejt.cacheservice.cache.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheservice.cache.Cache;
import org.yejt.cacheservice.cache.CacheExpirationManager;
import org.yejt.cacheservice.cache.expiration.LazyCacheExpirationManager;
import org.yejt.cacheservice.cache.expiration.NoOpCacheExpirationManager;
import org.yejt.cacheservice.cache.expiration.ScheduleCacheExpirationManager;
import org.yejt.cacheservice.constant.CacheExpirationConstants;
import org.yejt.cacheservice.constant.CacheTypeConstants;
import org.yejt.cacheservice.properties.CacheExpirationProperties;
import org.yejt.cacheservice.properties.CacheProperties;
import org.yejt.cacheservice.store.*;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author keys961
 */
public class XXCache implements Cache<String, byte[]> {
    private final Logger LOGGER = LoggerFactory.getLogger(XXCache.class);

    private DataStore<String, byte[]> dataStore;

    private CacheProperties properties;

    private CacheExpirationManager<String, byte[]> cacheExpirationManager;

    private volatile boolean isClosed;

    public XXCache(CacheProperties properties) {
        this.properties = properties;
        buildCache();
        LOGGER.info("XXCache - Cache {} is built.", properties.getCacheName());
    }

    private void buildCache() {
        buildDataStore();
        buildExpirationManager();
    }

    private void buildDataStore() {
        if (properties.getCacheType() == null) {
            dataStore = new BaseDataStore<>();
            return;
        }

        switch (properties.getCacheType()) {
            case CacheTypeConstants
                    .BASIC:
            case CacheTypeConstants.DEFAULT:
                dataStore = new BaseDataStore<>();
                break;
            case CacheTypeConstants.BOUNDED_BASIC:
                dataStore = new BoundedBaseDataStore<>(properties.getMaxSize());
                break;
            case CacheTypeConstants.FIFO:
                dataStore = new FifoDataStore<>(properties.getMaxSize());
                break;
            case CacheTypeConstants.LRU:
                dataStore = new LruDataStore<>(properties.getMaxSize());
                break;
            case CacheTypeConstants.APPROXIMATE_LRU:
                dataStore = new ApproximateLruDataStore<>(properties.getMaxSize());
                break;
            case CacheTypeConstants.WEAK_REFERENCE:
                dataStore = new WeakReferenceDataStore<>();
                break;
            default:
                dataStore = new BaseDataStore<>();
        }
    }

    private void buildExpirationManager() {
        CacheExpirationProperties expirationProperties = properties.getExpiration();
        if (expirationProperties == null || expirationProperties.getStrategy() == null) {
            cacheExpirationManager = new NoOpCacheExpirationManager<>();
            return;
        }

        switch (expirationProperties.getStrategy()) {
            case CacheExpirationConstants
                    .NOOP_STRATEGY:
                cacheExpirationManager = new NoOpCacheExpirationManager<>();
                break;
            case CacheExpirationConstants.LAZY_STRATEGY:
                cacheExpirationManager = new LazyCacheExpirationManager<>(dataStore, expirationProperties);
                break;
            case CacheExpirationConstants.SCHEDULE_STRATEGY:
                cacheExpirationManager = new ScheduleCacheExpirationManager<>(dataStore,
                        expirationProperties);
                break;
            default:
                cacheExpirationManager = new NoOpCacheExpirationManager<>();
        }
    }


    @Override
    public byte[] get(String key) {
        if (isClosed) {
            throw new RuntimeException("Cache is closed.");
        }

        byte[] v = cacheExpirationManager.filter(key, dataStore.get(key));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("XXCache - {}#get: key: {}, value: {}.", properties.getCacheName(),
                    key, v);
        }

        return v;
    }

    @Override
    public Map<String, byte[]> getAll(Set<? extends String> keys) {
        if (isClosed) {
            throw new RuntimeException("Cache is closed.");
        }
        Map<String, byte[]> kvMap = new HashMap<>(keys.size());
        keys.forEach(k -> kvMap.put(k, get(k)));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("XXCache - {}#getAll: keys: {}, values: {}.", properties.getCacheName(),
                    keys, kvMap);
        }
        return kvMap;
    }

    @Override
    public DataStore<String, byte[]> getDataStore() {
        return this.dataStore;
    }

    @Override
    public byte[] put(String key, byte[] value) {
        if (isClosed) {
            throw new RuntimeException("Cache is closed.");
        }
        ValueHolder<byte[]> v = dataStore.put(key, value);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("XXCache - {}#put: key: {}, value: {}.", properties.getCacheName(),
                    key, v);
        }
        if (v == null) {
            return null;
        }

        return v.value();
    }

    @Override
    public byte[] remove(String key) {
        if (isClosed) {
            throw new RuntimeException("Cache is closed.");
        }
        ValueHolder<byte[]> v = dataStore.remove(key);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("XXCache - {}#remove: key: {}, value: {}.", properties.getCacheName(),
                    key, v);
        }
        if (v == null) {
            return null;
        }

        return v.value();
    }

    @Override
    public boolean containsKey(String key) {
        if (isClosed) {
            throw new RuntimeException("Cache is closed.");
        }
        return get(key) != null;
    }

    @Override
    public void clear() {
        if (isClosed) {
            throw new RuntimeException("Cache is closed.");
        }
        LOGGER.warn("XXCache - {}#clear.");
        dataStore.clear();
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }
        isClosed = true;
        dataStore.clear();
        LOGGER.warn("XXCache - {}#clear.");
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public String getCacheName() {
        return properties.getCacheName();
    }

    @Override
    public CacheProperties getProperties() {
        return properties;
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        if (cls.isAssignableFrom(getClass())) {
            return cls.cast(this);
        }

        throw new IllegalArgumentException("Unwrapping to " + cls
                + " is not a supported by this implementation");
    }


}
