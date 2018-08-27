package org.yejt.cachedemo.keygen;

import org.yejt.cacheclient.keygen.KeyGenerator;
import org.yejt.cachedemo.entity.User;

public class UserKeyGenerator implements KeyGenerator
{
    @Override
    public String generateKey(Object target, Object... params)
    {
        return ((User)params[0]).getUsername();
    }
}
