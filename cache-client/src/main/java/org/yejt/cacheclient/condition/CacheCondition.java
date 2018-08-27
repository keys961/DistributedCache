package org.yejt.cacheclient.condition;

public interface CacheCondition
{
    boolean condition(Object result, Object... params);
}
