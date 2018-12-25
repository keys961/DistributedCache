package org.yejt.cacheservice.cache.expiration;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheservice.cache.CacheExpirationManager;
import org.yejt.cacheservice.properties.CacheExpirationProperties;
import org.yejt.cacheservice.store.DataStore;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled cache expiration manager, it will check the expiration periodically.
 *
 * @param <K>: Key type
 * @param <V>: Value type
 * @author keys961
 */
public class ScheduleCacheExpirationManager<K, V> implements CacheExpirationManager<K, V> {
    private final Logger LOGGER = LoggerFactory.getLogger(ScheduleCacheExpirationManager.class);
    private static final int THOUSANDS = 1000;
    private final long expiration;
    private DataStore<K, V> dataStore;

    public ScheduleCacheExpirationManager(DataStore<K, V> dataStore,
                                          CacheExpirationProperties properties) {
        this.dataStore = dataStore;
        this.expiration = properties.getExpiration();
        // time is all in second
        long initDelay = properties.getInitDelay();
        long interval = properties.getInterval();
        ScheduledExecutorService schedulePool = new ScheduledThreadPoolExecutor(1,
                new DefaultThreadFactory("expiration-scheduled-pool", true));
        schedulePool.scheduleAtFixedRate(this::scheduleClean,
                initDelay, interval, TimeUnit.SECONDS);
    }

    @Override
    public boolean isExpired(ValueHolder<V> v) {
        // no need to check the expiration..
        return false;
    }

    @Override
    public void remove(K key) {
        dataStore.remove(key);
    }

    private void scheduleClean() {
        long currentTime = System.currentTimeMillis();
        LOGGER.info("Clean expiration cache at {}.", currentTime);
        dataStore.getAll().forEach(e ->
                {
                    if (e.getValue().getTimestamp() < currentTime - expiration * THOUSANDS) {
                        remove(e.getKey());
                        LOGGER.info("Key {} is cleaned.", e.getValue());
                    }
                }
        );
    }
}
