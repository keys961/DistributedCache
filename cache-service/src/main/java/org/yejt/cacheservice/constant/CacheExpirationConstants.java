package org.yejt.cacheservice.constant;

/**
 * @author keys961
 */
public class CacheExpirationConstants {
    public static final long DEFAULT_EXPIRATION = Long.MAX_VALUE;

    public static final String NOOP_STRATEGY = "noop";

    public static final String LAZY_STRATEGY = "lazy";

    public static final String SCHEDULE_STRATEGY = "schedule";
}
