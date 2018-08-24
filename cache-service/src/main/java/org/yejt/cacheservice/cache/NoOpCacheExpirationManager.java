package org.yejt.cacheservice.cache;

import org.yejt.cacheservice.store.value.ValueHolder;

public class NoOpCacheExpirationManager<K, V> implements CacheExpirationManager<K, V>
{
    @Override
    public boolean isExpired(ValueHolder<V> v)
    {
        return false;
    }

    @Override
    public void remove(Object key)
    {
        // no-ops
    }
}
