package org.yejt.cacheclient.client.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yejt.cacheclient.client.XXCacheClient;

/**
 * Fall back client for {@link XXCacheClient}.
 *
 * @author keys961
 */
@Component
public class XXCacheClientFallback implements XXCacheClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(XXCacheClientFallback.class);

    @Override
    public byte[] get(String cacheName, String key) {
        LOGGER.warn("Fetch cache error from: {} with key: {}.",
                cacheName, key);

        return null;
    }

    @Override
    public byte[] put(String cacheName, String key, byte[] value) {
        LOGGER.warn("Put cache error from: {} with key: {}.",
                cacheName, key);
        return null;
    }

    @Override
    public String sayHello(String name) {
        return "error";
    }

    @Override
    public byte[] remove(String cacheName, String key) {
        LOGGER.warn("Remove cache error from: {} with key: {}.",
                cacheName, key);
        return null;
    }
}
