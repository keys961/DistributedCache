package org.yejt.cacheclient.annotation;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(XXCacheAutoConfig.class)
@EnableFeignClients(basePackages = "org.yejt.cacheclient.client")
public @interface XXCacheAutoConfiguration
{}
