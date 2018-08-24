package org.yejt.cacheservice.cache;

import org.yejt.cacheservice.store.value.ValueHolder;

public interface CacheExpirationManager<K, V>
{
    boolean isExpired(ValueHolder<V> v);

    default V filter(K key, ValueHolder<V> valueHolder)
    {
        if(key == null || valueHolder == null)
            return null;
        if(!isExpired(valueHolder))
            return valueHolder.value();
        // remove expired item
        remove(key);
        return null;
    }

    void remove(K key);
}
