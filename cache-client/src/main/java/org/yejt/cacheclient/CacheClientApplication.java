package org.yejt.cacheclient;

import org.springframework.boot.SpringApplication;

/**
 * This class may be deleted, just offered a client to
 * access the cache cluster.
 * These @EnableXXX annotations & application.yml configurations
 * must be added to the upper application using this caching service
 */
public class CacheClientApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(CacheClientApplication.class, args);
    }
}
