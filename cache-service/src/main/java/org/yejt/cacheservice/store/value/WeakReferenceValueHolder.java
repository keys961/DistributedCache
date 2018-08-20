package org.yejt.cacheservice.store.value;

import java.lang.ref.WeakReference;

public class WeakReferenceValueHolder<V> implements ValueHolder<V>
{
    private final WeakReference<V> value;

    public WeakReferenceValueHolder(V value)
    {
        this.value = new WeakReference<>(value);
    }

    @Override
    public V value()
    {
        return value.get();
    }
}
