package org.yejt.cacheservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestService
{
    @Value("${server.port}")
    private int port;

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "name", defaultValue = "forezp") String name)
    {
        return "hello, world!" + " " + port;
    }

    @PostMapping("/post")
    public String testPost(String name)
    {
        return name + port;
    }
}
