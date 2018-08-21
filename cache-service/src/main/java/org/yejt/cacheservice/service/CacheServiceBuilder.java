package org.yejt.cacheservice.service;

public class CacheServiceBuilder
{
    private CacheService cacheService = new CacheService();

    public CacheService build()
    {
        return cacheService;
    }

    //TODO: build with configurations...
}
