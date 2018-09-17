package org.yejt.cacheclient.config;

import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@Configuration
public class FeignProtobufConfig
{
//    @Autowired
//    private ObjectFactory<HttpMessageConverters> messageConverters;
//
//    @Bean
//    public Encoder encoder()
//    {
//        return new SpringEncoder(messageConverters);
//    }
//
//    @Bean
//    public Decoder decoder()
//    {
//        return new SpringDecoder(messageConverters);
//    }
//
//    @Bean
//    public ProtobufHttpMessageConverter protobufHttpMessageConverter()
//    {
//        return new ProtobufHttpMessageConverter();
//    }
}
