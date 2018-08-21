package org.yejt.cacheclient.keygen;

import java.lang.reflect.Method;

public interface KeyGenerator
{
    Object generateKey(Object target, Method method, Object... params);

    KeyGenerator create();
}
