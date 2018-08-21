package org.yejt.cacheclient.client;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "cache-route", fallback = TestClientHystrix.class, path = "/cache-service")
public interface TestClient
{
    // TODO: add param to IRule#choose(key)..
    @GetMapping("/hello")
    String sayHello(@RequestHeader(FilterConstants.LOAD_BALANCER_KEY) @RequestParam(value = "name")String name);

    @PostMapping("/post")
    String testPost(@RequestHeader(FilterConstants.LOAD_BALANCER_KEY) String name);
}
