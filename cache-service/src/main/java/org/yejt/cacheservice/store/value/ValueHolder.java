package org.yejt.cacheservice.store.value;

public interface ValueHolder<V>
{
    V value();

    long getTimestamp();
}
