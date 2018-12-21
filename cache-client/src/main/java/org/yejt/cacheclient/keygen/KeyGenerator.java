package org.yejt.cacheclient.keygen;

public interface KeyGenerator
{
    String generateKey(Object target, Object... params);
}
