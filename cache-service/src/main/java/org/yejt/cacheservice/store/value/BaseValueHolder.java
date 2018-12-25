package org.yejt.cacheservice.store.value;

/**
 * Base value holder.
 *
 * @param <V>: Value type
 * @author keys961
 */
public class BaseValueHolder<V> implements ValueHolder<V> {
    private final V value;

    private final long timestamp;

    public BaseValueHolder(V value) {
        this.value = value;
        timestamp = System.currentTimeMillis();
    }

    @Override
    public V value() {
        return value;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        if (value == null)
            return null;
        return value.toString();
    }
}
