package org.yejt.cacheservice.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component("cacheProperties")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheProperties
{
    private String cacheName;

    private String cacheType; // basic(default), random, fifo, lru, a-lru, weak-ref

    private long maxSize;

    private long expiration; // -1: no expiration applied

    private String keyClass;

    private String valueClass;
}
