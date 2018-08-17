package org.yejt.cacheservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestService
{
    @Value("${server.port}")
    private int port;

    @GetMapping("/hello")
    public String sayHello()
    {
        return "hello, world!" + " " + port;
    }
}
