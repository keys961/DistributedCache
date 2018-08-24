package org.yejt.cacheservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yejt.cacheservice.cache.CacheManager;

@Service
public class CacheService
{
    //TODO: Implement the cache service
    private CacheManager cacheManager;

    @Autowired
    public CacheService(CacheManager cacheManager)
    {
        this.cacheManager = cacheManager;
        System.out.println(cacheManager.getProperties());
    }
}
