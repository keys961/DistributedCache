package org.yejt.cacheservice.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yejt.cacheservice.service.CacheService;

@RestController
public class CacheEndpoint
{
    //TODO: Finish endpoint
    @Value("${server.port}")
    private int port;

    @Autowired
    private CacheService cacheService;

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "name", defaultValue = "fucker") String name)
    {
        return "hello," + name + "!" + " FROM: " + port;
    }

    @GetMapping(value = "/{cacheName}/{key}")
    ResponseEntity get(@PathVariable String cacheName, @PathVariable String key)
    {
        return ResponseEntity.ok("OK, dummy response!");
    }

    /**
     * This method will cause cache overwritten
     */
    @PostMapping(value = "/{cacheName}/{key}")
    ResponseEntity put(@PathVariable String cacheName, @PathVariable String key,
             @RequestBody Object value)
    {
        return ResponseEntity.ok("OK, dummy response!");
    }

    @DeleteMapping(value = "/{cacheName}/{key}")
    ResponseEntity remove(@PathVariable String cacheName, @PathVariable String key)
    {
        return ResponseEntity.ok("OK, dummy response!");
    }

}
