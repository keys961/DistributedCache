package org.yejt.cacheclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yejt.cacheclient.client.XXCacheClient;

@RestController
public class TestService
{
    @Autowired
    private XXCacheClient client;

    @GetMapping("/hello")
    public String sayHello(@RequestParam(name = "name") String name)
    {
        return client.sayHello(name);
    }

}
