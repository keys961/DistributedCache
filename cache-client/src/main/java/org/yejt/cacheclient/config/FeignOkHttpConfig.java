package org.yejt.cacheclient.config;

import feign.Feign;
import okhttp3.ConnectionPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class FeignOkHttpConfig
{
    @Value("${xxcache.client.readTimeout:2000}")
    private int readTimeout;

    @Value("${xxcache.client.connectTimeout:2000}")
    private int connectTimeout;

    @Value("${xxcache.client.writeTimeout:5000}")
    private int writeTimeout;

    @Value("${xxcache.client.pool.maxIdleConnection:16}")
    private int maxIdleConnection;

    @Value("${xxcache.client.pool.keepAliveDuration:300}")
    private int keepAliveDuration;

    @Bean
    public okhttp3.OkHttpClient okHttpClient()
    {
        return new okhttp3.OkHttpClient.Builder()
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(maxIdleConnection,
                        keepAliveDuration, TimeUnit.SECONDS))
                .build();
    }
}
