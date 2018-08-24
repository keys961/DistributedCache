package org.yejt.cacheservice.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheservice.properties.CacheExpirationProperties;
import org.yejt.cacheservice.store.DataStore;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LazyCacheExpirationManager<K> implements CacheExpirationManager<K>
{
    private DataStore<K, ?> dataStore;

    private CacheExpirationProperties properties;

    private HashMap<K, Long> timestampMap;

    private ReentrantLock lock = new ReentrantLock();

    private final Logger LOGGER = LoggerFactory.getLogger(LazyCacheExpirationManager.class);

    private static final long AWAIT_TIMEOUT = 10000;

    public LazyCacheExpirationManager(DataStore<K, ?> dataStore, CacheExpirationProperties properties)
    {
        this.dataStore = dataStore;
        this.properties = properties;
        this.timestampMap = new HashMap<>();
    }

    @Override
    public boolean isExpired(K key)
    {
        boolean status = true;
        lock.lock();
        if(timestampMap.get(key) != null &&
                timestampMap.get(key) < System.currentTimeMillis() - properties.getExpiration())
        {
            timestampMap.remove(key);
            dataStore.remove(key);
            status = false;
        }
        lock.unlock();
        return status;
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
    public void clearTimestamp()
    {
        lock.lock();
        timestampMap.clear();
        lock.unlock();
    }

    @Override
    public void scheduleClear()
    {
        //no-ops
    }
}
