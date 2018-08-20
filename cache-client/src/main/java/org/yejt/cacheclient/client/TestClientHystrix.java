package org.yejt.cacheclient.client;

import org.springframework.stereotype.Component;
import org.yejt.cacheclient.client.TestClient;

@Component
public class TestClientHystrix implements TestClient
{
    @Override
    public String sayHello(String name)
    {
        return "null";
    }

    @Override
    public String testPost(String name)
    {
        return "null";
    }
}
