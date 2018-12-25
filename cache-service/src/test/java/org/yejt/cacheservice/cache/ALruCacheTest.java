package org.yejt.cacheservice.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yejt.cacheservice.CacheServiceApplicationTests;
import org.yejt.cacheservice.cache.manager.XXCacheManager;
import org.yejt.cacheservice.constant.CacheExpirationConstants;
import org.yejt.cacheservice.constant.CacheTypeConstants;
import org.yejt.cacheservice.properties.CacheExpirationProperties;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class ALruCacheTest extends CacheServiceApplicationTests {
    private CacheManager cacheManager;

    private CacheManagerProperties properties;

    private Map<String, String> dataSet = new HashMap<>();

    private CountDownLatch countDownLatch = new CountDownLatch(32);

    private void buildProperties() {
        properties = new CacheManagerProperties();
        properties.setCaches(new ArrayList<>());
        // Approximate Lru - Lazy expirations
        CacheProperties properties2 = new CacheProperties();
        properties2.setCacheName("cache2");
        properties2.setMaxSize(10240);
        properties2.setCacheType(CacheTypeConstants.APPROXIMATE_LRU);
        properties2.setExpiration(
                new CacheExpirationProperties(
                        10, CacheExpirationConstants.LAZY_STRATEGY,
                        0L, 6000L
                )
        );
        properties.getCaches().add(properties2);
    }

    private void buildDataSet() {
        IntStream.range(1, 20480)
                .forEach(i -> dataSet.put(String.valueOf(i), String.valueOf(-i)));
    }

    @Before
    public void beforeTest() {
        buildProperties();
        buildDataSet();
        cacheManager = new XXCacheManager(properties);
    }

    @Test
    public void testMultithread() throws InterruptedException {
        final int writeCount = 16;
        final int readCount = 16;
        for (int i = 0; i < writeCount; i++)
            new Thread(new PutTask()).start();
        for (int i = 0; i < readCount; i++)
            new Thread(new QueryTask()).start();

        countDownLatch.await();
    }

    class PutTask implements Runnable {
        @Override
        public void run() {
            Cache cache = cacheManager.getCache("cache2");
            dataSet.forEach(cache::put);
            countDownLatch.countDown();
        }
    }

    class QueryTask implements Runnable {
        @Override
        public void run() {
            Cache cache = cacheManager.getCache("cache2");
            dataSet.forEach((k, v) ->
            {
                String value = (String) cache.get(k);
                if (value != null)
                    Assert.assertEquals(v, value);
            });
            countDownLatch.countDown();
        }
    }
}
