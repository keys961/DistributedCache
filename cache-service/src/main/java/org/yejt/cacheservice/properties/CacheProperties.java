package org.yejt.cacheservice.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheProperties
{
    private String cacheName;

    private String cacheType = "basic"; // basic(default), random, fifo, lru, a-lru, weak-ref

    private long maxSize = Long.MAX_VALUE;

    private CacheExpirationProperties expiration = new CacheExpirationProperties(
            Long.MAX_VALUE, "noop"
    );

    private String keyClass = "java.lang.String";

    private String valueClass;
}
