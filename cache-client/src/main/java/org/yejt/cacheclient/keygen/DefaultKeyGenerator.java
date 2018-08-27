package org.yejt.cacheclient.keygen;

import java.lang.reflect.Method;

public class DefaultKeyGenerator implements KeyGenerator
{
    @Override
    public String generateKey(Object target, Object... params)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for(Object param : params)
            stringBuilder.append(param.toString());

        return stringBuilder.toString();
    }
}
