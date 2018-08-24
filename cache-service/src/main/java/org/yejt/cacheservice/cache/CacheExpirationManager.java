package org.yejt.cacheservice.cache;

public interface CacheExpirationManager<K>
{
    boolean isExpired(K key);

    default K filter(K key)
    {
        if(!isExpired(key))
            return key;
        return null;
    }

    void updateTimestamp(K key);

    void removeTimestamp(K key);

    void clearTimestamp();

    void scheduleClear();
}
