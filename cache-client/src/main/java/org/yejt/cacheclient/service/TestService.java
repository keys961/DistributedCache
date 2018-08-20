package org.yejt.cacheclient.service;

import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;
import org.yejt.cacheclient.client.TestClient;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestService
{
    @Autowired
    private TestClient client;

    @GetMapping("/hello")
    public String sayHello(@RequestParam(name = "name") String name, HttpServletRequest request)
    {
        return client.sayHello(name);
    }

    @PostMapping("/post")
    public String testPost(@RequestBody String name)
    {
        return client.testPost(name);
    }

}
