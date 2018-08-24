package org.yejt.cacheservice.cache;

public class NoOpCacheExpirationManager<K> implements CacheExpirationManager<K>
{
    @Override
    public boolean isExpired(K key)
    {
        return true;
    }

    @Override
    public void updateTimestamp(K key)
    {
        // no-ops
    }

    @Override
    public void removeTimestamp(K key)
    {
        // no-ops
    }

    @Override
    public void scheduleClear()
    {
        // no-ops
    }
}
