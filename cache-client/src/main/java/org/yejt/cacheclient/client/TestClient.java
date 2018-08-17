package org.yejt.cacheclient.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "cache-service", fallback = TestClientHystrix.class)
public interface TestClient
{
    @GetMapping("/hello")
    String sayHello();
}
