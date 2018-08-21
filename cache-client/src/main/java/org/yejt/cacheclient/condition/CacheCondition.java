package org.yejt.cacheclient.condition;

public interface CacheCondition
{
    boolean condition(Object result);

    /**
     * Please implement SINGLETON mode
     */
    CacheCondition create();
}
