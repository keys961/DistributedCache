package org.yejt.cacheservice.store.value;

public class BaseValueHolder<V> implements ValueHolder<V>
{
    private final V value;

    public BaseValueHolder(V value)
    {
        this.value = value;
    }

    @Override
    public V value()
    {
        return value;
    }
}
