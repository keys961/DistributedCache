package org.yejt.cacheclient.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "cache-service", fallback = TestClientHystrix.class)
public interface TestClient
{
    // TODO: add param to IRule#choose(key)..
    @GetMapping("/hello")
    String sayHello(@RequestParam(value = "name")String name);

    @PostMapping("/post")
    String testPost(String name);
}
