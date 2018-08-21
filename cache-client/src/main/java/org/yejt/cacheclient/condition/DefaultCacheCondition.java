package org.yejt.cacheclient.condition;

public class DefaultCacheCondition implements CacheCondition
{
    private static class Holder
    {
        private static DefaultCacheCondition cacheCondition = new DefaultCacheCondition();
    }

    private DefaultCacheCondition() { }

    @Override
    public boolean condition(Object result)
    {
        return true;
    }

    @Override
    public CacheCondition create()
    {
        return Holder.cacheCondition;
    }
}
