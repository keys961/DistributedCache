package org.yejt.cacheclient.condition;

/**
 * @author keys961
 */
public class DefaultCacheCondition implements CacheCondition {
    @Override
    public boolean condition(Object result, Object... params) {
        return true;
    }
}
