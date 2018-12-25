package org.yejt.cacheclient.codec;

import com.esotericsoftware.kryo.Kryo;

/**
 * <p>Codec interface for data encoding and decoding.</p
 * <p>Default using {@link Kryo} in </p>
 *
 * @param <T>: Value type
 * @author keys961
 */
public interface CacheCodec<T> {

    /**
     * Encode the given object to <code>byte[]</code> raw value.
     *
     * @param value: Given object to encode
     * @return <code>byte[]</code> encoded raw value of the given object
     */
    byte[] encode(T value);

    /**
     * <p>Decode the given <code>byte[]</code> raw value to the object with
     *  type <code>T</code>.</p>
     *
     * @param bytes: Raw value
     * @return Decoded value with type <code>T</code>
     */
    T decode(byte[] bytes);
}
