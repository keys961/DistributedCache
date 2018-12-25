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
public class CacheExpirationProperties {
    /**
     * All in second
     */
    private long expiration = Long.MAX_VALUE;

    private String strategy;

    private long initDelay = 0L;

    private long interval = 600L;
}
