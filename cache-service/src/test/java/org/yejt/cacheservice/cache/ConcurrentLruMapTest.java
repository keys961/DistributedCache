package org.yejt.cacheservice.cache;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ConcurrentLruMapTest
{
    private ConcurrentHashMap<Integer, String> map =
            new ConcurrentHashMap<>();

    @Test
    public void test()
    {
        IntStream.range(0, 10).forEach(i -> map.put(i, Integer.toString(-i)));
        Set<Map.Entry<Integer, String>> set = map.entrySet();

        Logger.getGlobal().info(set.toString());

        Assert.assertEquals(10, set.size());
    }
}
