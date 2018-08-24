package org.yejt.cacheservice.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheservice.properties.CacheExpirationProperties;
import org.yejt.cacheservice.store.DataStore;

import java.io.Closeable;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class ScheduleCacheExpirationManager<K> implements CacheExpirationManager<K>, Closeable
{
    private DataStore<K, ?> dataStore;

    private CacheExpirationProperties properties;

    private HashMap<K, Long> timestampMap;

    private final ScheduledExecutorService SCHEDULE
            = Executors.newScheduledThreadPool(1);

    private ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER = LoggerFactory.getLogger(ScheduleCacheExpirationManager.class);

    private static final long AWAIT_TIMEOUT = 10000;

    public ScheduleCacheExpirationManager(DataStore<K, ?> dataStore, CacheExpirationProperties properties)
    {
        this.dataStore = dataStore;
        this.properties = properties;
        timestampMap = new HashMap<>();
        SCHEDULE.scheduleAtFixedRate(this::scheduleClear,
            properties.getInitialDelay(), properties.getExpiration(), TimeUnit.SECONDS);
    }

    @Override
    public boolean isExpired(K key)
    {
        return true;
    }

    @Override
    public void updateTimestamp(K key)
    {
        lock.lock();
        timestampMap.put(key, System.currentTimeMillis());
        lock.unlock();
    }

    @Override
    public void removeTimestamp(K key)
    {
        lock.lock();
        timestampMap.remove(key);
        lock.unlock();
    }

    @Override
    public void scheduleClear()
    {
        lock.lock();
        long currentTime = System.currentTimeMillis();
        timestampMap.forEach((k, t) ->
        {
            if(t < currentTime - properties.getExpiration())
            {
                // Expiration unit is "second"
                if(timestampMap.get(k) != null
                        && timestampMap.get(k) < currentTime - properties.getExpiration() * 1000)
                {
                    dataStore.remove(k);
                    timestampMap.remove(k);
                }

            }
        });
        lock.unlock();
    }

    @Override
    public void clearTimestamp()
    {
        lock.lock();
        timestampMap.clear();
        lock.unlock();
    }

    @Override
    public void close()
    {
        try
        {
            SCHEDULE.shutdown();
            if(!SCHEDULE.awaitTermination(AWAIT_TIMEOUT, TimeUnit.MILLISECONDS))
                SCHEDULE.shutdownNow();
        }
        catch (InterruptedException e)
        {
            LOGGER.warn("Interrupted when await termination.");
            SCHEDULE.shutdownNow();
        }

        LOGGER.info("Scheduler has been shut down.");
    }
}
