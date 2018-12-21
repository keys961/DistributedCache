package org.yejt.cacheservice.store;

import org.junit.Assert;
import org.junit.Test;
import org.yejt.cacheservice.store.container.ConcurrentLRUHashMap;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ConcurrentLruMapTest
{
    private ConcurrentLRUHashMap<Integer, String> map =
            new ConcurrentLRUHashMap<>();

    @Test
    public void test()
    {
        IntStream.range(0, 10).forEach(i -> map.put(i, Integer.toString(-i)));
        Set<Map.Entry<Integer, String>> set = map.entrySet();

        Logger.getGlobal().info(set.toString());

        Assert.assertEquals(9, set.size());
    }
}
