package org.yejt.cacheclient.keygen;

import java.lang.reflect.Method;

public class DefaultKeyGenerator implements KeyGenerator
{
    private static class Holder
    {
        private static DefaultKeyGenerator generator = new DefaultKeyGenerator();
    }

    private static final Object DEFAULT_KEY = new Object();

    private DefaultKeyGenerator() {}

    @Override
    public Object generateKey(Object target, Method method, Object... params)
    {
        return null;
    }

    @Override
    public KeyGenerator create()
    {
        return Holder.generator;
    }
}
