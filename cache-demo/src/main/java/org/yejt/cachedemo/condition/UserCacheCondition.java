package org.yejt.cachedemo.condition;

import org.yejt.cacheclient.condition.CacheCondition;
import org.yejt.cachedemo.entity.User;

/**
 * @author keys961
 */
public class UserCacheCondition implements CacheCondition {
    @Override
    public boolean condition(Object result, Object... params) {
        User user = (User) result;
        return user != null && user.getUsername() != null &&
                !"keys961".equals(user.getUsername());
    }
}
