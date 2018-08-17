package org.yejt.cacheclient.client;

import org.springframework.stereotype.Component;
import org.yejt.cacheclient.client.TestClient;

@Component
public class TestClientHystrix implements TestClient
{
    @Override
    public String sayHello()
    {
        return "null";
    }
}
