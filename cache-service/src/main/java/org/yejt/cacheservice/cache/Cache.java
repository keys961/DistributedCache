package org.yejt.cacheservice.cache;

import org.yejt.cacheservice.properties.CacheProperties;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;

public interface Cache<K, V> extends Closeable
{
    V get(K key);

    Map<K, V> getAll(Set<? extends K> keys);

    V put(K key, V value);

    V remove(K key);

    boolean containsKey(K key);

    void clear();

    void close();

    boolean isClosed();

    String getCacheName();

    CacheProperties getProperties();

    <T> T unwrap(Class<T> cls);
}
