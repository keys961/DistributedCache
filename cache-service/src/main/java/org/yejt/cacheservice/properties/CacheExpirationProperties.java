package org.yejt.cacheservice.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheExpirationProperties
{
    private long expiration = Long.MAX_VALUE; // in second

    private String strategy;

    private long initDelay = 0L;

    private long interval = 600L;
}
