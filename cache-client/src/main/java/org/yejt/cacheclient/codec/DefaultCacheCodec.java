package org.yejt.cacheclient.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class DefaultCacheCodec implements CacheCodec<Object> {

    private KryoPool pool = new KryoPool.Builder(Kryo::new).build();

    @Override
    public byte[] encode(Object value)
    {
        Kryo kryo = null;
        byte[] rawValue = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
        {
            kryo = pool.borrow();
            Output output = new Output(byteArrayOutputStream);
            kryo.writeClassAndObject(output, value);
            rawValue = output.toBytes();
            output.close();
        }
        catch (Exception e)
        {
            //no-op
            e.printStackTrace();
        }
        finally
        {
            if(kryo != null)
                pool.release(kryo);
        }
        return rawValue;
    }

    @Override
    public Object decode(byte[] bytes)
    {
        Kryo kryo = null;
        Object value = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes))
        {
            Input input = new Input(byteArrayInputStream);
            kryo = pool.borrow();
            value = kryo.readClassAndObject(input);
            input.close();
        }
        catch (Exception e)
        {
            // no-op
            e.printStackTrace();
        }
        finally
        {
            if(kryo != null)
                pool.release(kryo);
        }
        return value;
    }
}
