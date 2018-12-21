package org.yejt.cacheservice.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yejt.cacheservice.properties.CacheManagerProperties;
import org.yejt.cacheservice.properties.CacheProperties;
import org.yejt.cacheservice.service.CacheService;

@RestController
@SuppressWarnings("unchecked")
public class CacheEndpoint
{
    @Value("${server.port}")
    private int port;

    private CacheService cacheService;

    @Autowired
    public CacheEndpoint(CacheService cacheService)
    {
        this.cacheService = cacheService;
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "name", defaultValue = "fucker") String name)
    {
        return "hello," + name + "!" + " FROM: " + port;
    }

    @GetMapping(value = "/{cacheName}/{key}")
    public Object get(@PathVariable String cacheName, @PathVariable String key)
    {
        return cacheService.get(cacheName, key).orElse(null);
    }

    /**
     * This method will cause cache overwritten
     */
    @PostMapping(value = "/{cacheName}/{key}")
    public Object put(@PathVariable String cacheName, @PathVariable String key,
             @RequestBody Object value)
    {
        return cacheService.put(cacheName, key, value).orElse(null);
    }

    @DeleteMapping(value = "/{cacheName}/{key}")
    public Object remove(@PathVariable String cacheName, @PathVariable String key)
    {
        return cacheService.remove(cacheName, key).orElse(null);
    }

    @GetMapping(value = "/props")
    public ResponseEntity<CacheManagerProperties> getProperties()
    {
        return ResponseEntity.ok(cacheService.getManagerProperties());
    }

    @GetMapping(value = "/props/{cacheName}")
    public ResponseEntity<CacheProperties> getCacheProperties(@PathVariable String cacheName)
    {
        return ResponseEntity.ok(cacheService.getCacheProperties(cacheName));
    }

    @GetMapping(value = "/isClosed")
    public ResponseEntity<Boolean> isClosed()
    {
        return ResponseEntity.ok(cacheService.isClosed());
    }

    @GetMapping(value = "/isClosed/{cacheName}")
    public ResponseEntity<Boolean> isClosed(@PathVariable String cacheName)
    {
        return ResponseEntity.ok(cacheService.isClosed(cacheName));
    }
}
