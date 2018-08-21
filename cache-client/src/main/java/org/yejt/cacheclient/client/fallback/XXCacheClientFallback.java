package org.yejt.cacheclient.client.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.yejt.cacheclient.client.XXCacheClient;


@Component
public class XXCacheClientFallback implements XXCacheClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XXCacheClientFallback.class);

    @Override
    public ResponseEntity get(String cacheName, String key)
    {
        LOGGER.warn("Fetch cache error from: {} with key: {}.",
                cacheName, key);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity put(String cacheName, String key, Object value)
    {
        LOGGER.warn("Put cache error from: {} with key: {}.",
                cacheName, key);
        return ResponseEntity.noContent().build();
    }

    @Override
    public String sayHello(String name)
    {
        return "error";
    }

    @Override
    public ResponseEntity remove(String cacheName, String key)
    {
        LOGGER.warn("Remove cache error from: {} with key: {}.",
                cacheName, key);
        return ResponseEntity.noContent().build();
    }
}
