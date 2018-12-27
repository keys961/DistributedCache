package org.yejt.cacheservice.store.value;

/**
 * Value holder interface that holds the value
 *
 * @param <V>: Value type
 * @author keys961
 */
public interface ValueHolder<V> {
    /**
     * Get the value it holds.
     *
     * @return Value that holds
     */
    V value();

    /**
     * Get the timestamp of the value
     * @return Timestamp of the value
     */
    long getTimestamp();

    /**
     * Get the value size of the value, default 1 as the value count.
     * @return The value size
     */
    default int getSize() {
        return 1;
    }
}
