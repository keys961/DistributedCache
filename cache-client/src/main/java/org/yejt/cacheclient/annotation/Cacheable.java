package org.yejt.cacheclient.annotation;

import org.yejt.cacheclient.condition.CacheCondition;
import org.yejt.cacheclient.condition.DefaultCacheCondition;
import org.yejt.cacheclient.keygen.DefaultKeyGenerator;
import org.yejt.cacheclient.keygen.KeyGenerator;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable
{
    String[] cacheName() default {};

    String key() default "";

    long expiration() default -1L;

    Class<? extends KeyGenerator> keyGenerator() default DefaultKeyGenerator.class;

    Class<? extends CacheCondition> condition() default DefaultCacheCondition.class;
}
