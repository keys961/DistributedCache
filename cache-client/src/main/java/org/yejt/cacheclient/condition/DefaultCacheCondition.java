package org.yejt.cacheclient.condition;

public class DefaultCacheCondition implements CacheCondition {
    @Override
    public boolean condition(Object result, Object... params) {
        return true;
    }
}
