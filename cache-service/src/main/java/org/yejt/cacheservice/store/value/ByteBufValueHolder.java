package org.yejt.cacheservice.store.value;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.*;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ByteBufValueHolder<V> implements ValueHolder<V>
{
    private static final KryoPool KRYO_POOL
            = new KryoPool.Builder(() ->
        {
            Kryo kryo = new Kryo();
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);
            ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                    .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }).build();

    private final long timestamp;

    private final byte[] buffer;

    public ByteBufValueHolder(V value)
    {
        buffer = storeValueToBuffer(value);
        timestamp = System.currentTimeMillis();
    }

    private byte[] storeValueToBuffer(V value)
    {
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();

        Output out = new Output(byteArrayOutputStream);
        try
        {
            Kryo kryo = KRYO_POOL.borrow();

            try
            {
                kryo.writeClassAndObject(out, value);
                out.flush();
                return byteArrayOutputStream.toByteArray();
            }
            finally
            {
                KRYO_POOL.release(kryo);
            }
        }
        finally
        {
            out.close();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V value()
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);

        Input input = new Input(byteArrayInputStream);
        Kryo kryo = KRYO_POOL.borrow();
        try
        {
            try
            {
                return (V) kryo.readClassAndObject(input);
            }
            finally
            {
                KRYO_POOL.release(kryo);
            }
        }
        finally
        {
            input.close();
        }
    }

    @Override
    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public int getSize()
    {
        return buffer.length;
    }

    private int getSize(int initSize)
    {
        int size = 1;
        while(size < initSize)
            size <<= 1;
        return size;
    }
}
