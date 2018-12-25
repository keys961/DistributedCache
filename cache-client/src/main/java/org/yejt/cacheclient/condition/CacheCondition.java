package org.yejt.cacheclient.condition;

import org.yejt.cacheclient.annotation.CachePut;
import org.yejt.cacheclient.annotation.CacheRemove;
import org.yejt.cacheclient.annotation.Cacheable;

/**
 * Cache condition/predicate used for caching
 *
 * @author keys961
 */
public interface CacheCondition {

    /**
     * <p>A predicate for caching.</p>
     *
     * <p>If used in {@link Cacheable} and {@link CachePut}, it will put
     * the value into the cache when the predicate returns <code>true</code>.
     * </p>
     *
     * <p>If used in {@link CacheRemove}, it will remove the value from the
     * cache when the predicate returns <code>true</code>.
     * </p>
     *
     * @param result: Method return value
     * @param params: Method params
     * @return The predicate value
     */
    boolean condition(Object result, Object... params);
}
