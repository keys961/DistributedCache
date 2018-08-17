package org.yejt.cacheclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yejt.cacheclient.client.TestClient;

@RestController
public class TestService
{
    @Autowired
    private TestClient client;

    @GetMapping("/hello")
    public String sayHello()
    {
        return client.sayHello();
    }

}
