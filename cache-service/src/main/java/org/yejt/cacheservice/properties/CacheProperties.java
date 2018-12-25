package org.yejt.cacheservice.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author keys961
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheProperties {
    private String cacheName;

    /**
     * Can be: basic(default), random, fifo, lru, a-lru, weak-ref
     */
    private String cacheType = "basic";

    private long maxSize = Long.MAX_VALUE;

    private CacheExpirationProperties expiration = new CacheExpirationProperties(
            Long.MAX_VALUE, "noop", 0L, 6000L
    );

    private String keyClass = "java.lang.String";

    private String valueClass;
}
