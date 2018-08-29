package org.yejt.cacheservice.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheservice.properties.CacheExpirationProperties;
import org.yejt.cacheservice.store.DataStore;
import org.yejt.cacheservice.store.value.ValueHolder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleCacheExpirationManager<K, V> implements CacheExpirationManager<K, V>
{
    private final Logger LOGGER = LoggerFactory.getLogger(ScheduleCacheExpirationManager.class);

    private DataStore<K, V> dataStore;

    private final long interval; // in second

    private final long initDelay; // in second

    private final long expiration; // in second

    private final ScheduledExecutorService SCHEDULE
            = Executors.newSingleThreadScheduledExecutor();

    public ScheduleCacheExpirationManager(DataStore<K, V> dataStore,
                                          CacheExpirationProperties properties)
    {
        this.dataStore = dataStore;
        this.expiration = properties.getExpiration();
        this.initDelay = properties.getInitDelay();
        this.interval = properties.getInterval();
        SCHEDULE.scheduleAtFixedRate(this::scheduleClean,
                initDelay, interval, TimeUnit.SECONDS);
    }

    @Override
    public boolean isExpired(ValueHolder<V> v)
    {
        // no need to check the expiration..
        return false;
    }

    @Override
    public void remove(K key)
    {
        dataStore.remove(key);
    }

    private void scheduleClean()
    {
        long currentTime = System.currentTimeMillis();
        LOGGER.info("Clean expiration cache at {}.", currentTime);
        dataStore.getAll().forEach(e ->
            {
                if(e.getValue().getTimestamp() < currentTime - expiration * 1000)
                {
                    remove(e.getKey());
                    LOGGER.info("Key {} is cleaned.", e.getValue());
                }
            }
        );
    }
}
