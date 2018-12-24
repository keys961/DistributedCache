package org.yejt.cacheclient.codec;

public interface CacheCodec<T>
{
    byte[] encode(T value);

    T decode(byte[] bytes);
}
