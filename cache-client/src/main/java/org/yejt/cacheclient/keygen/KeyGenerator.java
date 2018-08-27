package org.yejt.cacheclient.keygen;

import java.lang.reflect.Method;

public interface KeyGenerator
{
    String generateKey(Object target, Object... params);
}
