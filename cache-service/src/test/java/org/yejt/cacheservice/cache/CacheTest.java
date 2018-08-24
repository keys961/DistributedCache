package org.yejt.cacheservice.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yejt.cacheservice.CacheServiceApplicationTests;
import org.yejt.cacheservice.constant.CacheExpirationConstants;
import org.yejt.cacheservice.constant.CacheTypeConstants;
import org.yejt.cacheservice.properties.CacheExpirationProperties;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class CacheTest extends CacheServiceApplicationTests
{
    private CacheManager cacheManager;

    private CacheManagerProperties properties;

    private Map<String, String> dataSet = new HashMap<>();

    private void buildProperties()
    {
        properties = new CacheManagerProperties();
        properties.setCaches(new ArrayList<>());
        // Basic cache - no expirations
        CacheProperties properties1 = new CacheProperties();
        properties1.setCacheName("cache1");
        properties1.setMaxSize(Long.MAX_VALUE);
        properties1.setCacheType(CacheTypeConstants.BASIC);
        properties1.setExpiration(new CacheExpirationProperties(
                Long.MAX_VALUE, CacheExpirationConstants.NOOP_STRATEGY
        ));
        // Approximate Lru - Lazy expirations
        CacheProperties properties2 = new CacheProperties();
        properties2.setCacheName("cache2");
        properties2.setMaxSize(1024);
        properties2.setCacheType(CacheTypeConstants.APPROXIMATE_LRU);
        properties2.setExpiration(
                new CacheExpirationProperties(
                        10, CacheExpirationConstants.LAZY_STRATEGY
                )
        );
        properties.getCaches().add(properties1);
        properties.getCaches().add(properties2);
    }

    private void buildDataSet()
    {
        IntStream.range(1, 2048).forEach(i -> dataSet.put(String.valueOf(i), String.valueOf(-i)));
    }

    @Before
    public void beforeTest()
    {
        buildProperties();
        buildDataSet();
        cacheManager = new XXCacheManager(properties);
    }

    @Test
    public void testCache1()
    {
        Cache cache = cacheManager.getCache("cache1");
        dataSet.forEach(cache::put);

        for(String key : dataSet.keySet())
        {
            String value = (String) cache.get(key);
            Assert.assertEquals(dataSet.get(key), value);
        }
    }

    @Test
    public void testCache1Multithread() throws InterruptedException
    {
        Cache cache = cacheManager.getCache("cache1");
        Thread thread1 = new Thread(() -> dataSet.forEach(cache::put));
        Thread thread2 = new Thread(() -> dataSet.forEach(cache::put));
        Thread thread3 = new Thread(() -> dataSet.forEach(cache::put));
        Thread thread4 = new Thread(() -> dataSet.forEach(cache::put));

        thread1.start(); thread2.start(); thread3.start(); thread4.start();
        thread1.join(); thread2.join(); thread3.join(); thread4.join();

        thread1 = new Thread(() -> {
            for(String key : dataSet.keySet())
            {
                String value = (String) cache.get(key);
                Assert.assertEquals(dataSet.get(key), value);
            }
        });
        thread2 = new Thread(() -> {
            for(String key : dataSet.keySet())
            {
                String value = (String) cache.get(key);
                Assert.assertEquals(dataSet.get(key), value);
            }
        });
        thread3 = new Thread(() -> {
            for(String key : dataSet.keySet())
            {
                String value = (String) cache.get(key);
                Assert.assertEquals(dataSet.get(key), value);
            }
        });
        thread4 = new Thread(() -> {
            for(String key : dataSet.keySet())
            {
                String value = (String) cache.get(key);
                Assert.assertEquals(dataSet.get(key), value);
            }
        });

        thread1.start(); thread2.start(); thread3.start(); thread4.start();
        thread1.join(); thread2.join(); thread3.join(); thread4.join();
    }

    @Test
    public void testCache2()
    {
        Cache cache = cacheManager.getCache("cache2");
        dataSet.forEach(cache::put);
        int count = 0;

        for(String key : dataSet.keySet())
        {
            String value = (String) cache.get(key);
            if(value != null)
                count++;
        }

        Assert.assertTrue(1024 >= count);
    }

    @Test
    public void testCache2Expiration() throws InterruptedException
    {
        Cache cache = cacheManager.getCache("cache2");
        dataSet.forEach(cache::put);

        Thread.sleep(12000);

        for(String key : dataSet.keySet())
        {
            String value = (String) cache.get(key);
            Assert.assertNull(value);
        }
    }

    @Test
    public void testCache2Multithread() throws InterruptedException
    {
        Cache cache = cacheManager.getCache("cache2");
        Object dummy = new Object();
        ConcurrentHashMap<String, Object> set = new ConcurrentHashMap<>();
        Thread thread1 = new Thread(() -> dataSet.forEach(cache::put));
        Thread thread2 = new Thread(() -> {
            for(String key : dataSet.keySet())
            {
                String value = (String) cache.get(key);
                if(value != null)
                    set.put(value, dummy);
            }
        });
        Thread thread3 = new Thread(() -> dataSet.forEach(cache::put));
        Thread thread4 = new Thread(() -> {
            for(String key : dataSet.keySet())
            {
                String value = (String) cache.get(key);
                if(value != null)
                    set.put(value, dummy);
            }
        });

        thread1.start(); thread2.start(); thread3.start(); thread4.start();
        thread1.join(); thread2.join(); thread3.join(); thread4.join();

        thread1 = new Thread(() -> {
            for(String key : dataSet.keySet())
            {
                String value = (String) cache.get(key);
                if(value != null)
                    set.put(value, dummy);
            }
        });
        thread2 = new Thread(() -> dataSet.forEach(cache::put));
        thread3 = new Thread(() -> {
            for(String key : dataSet.keySet())
            {
                String value = (String) cache.get(key);
                if(value != null)
                    set.put(value, dummy);
            }
        });
        thread4 = new Thread(() -> dataSet.forEach(cache::put));

        thread1.start(); thread2.start(); thread3.start(); thread4.start();
        thread1.join(); thread2.join(); thread3.join(); thread4.join();

        Assert.assertTrue(2048 >= set.size());
        System.out.println(set.size() / 2048.0);
    }

}
