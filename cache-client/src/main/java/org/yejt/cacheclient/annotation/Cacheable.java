package org.yejt.cacheclient.annotation;

import org.yejt.cacheclient.condition.CacheCondition;
import org.yejt.cacheclient.condition.DefaultCacheCondition;
import org.yejt.cacheclient.keygen.DefaultKeyGenerator;
import org.yejt.cacheclient.keygen.KeyGenerator;

import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable
{
    String cacheName();

    Class<? extends KeyGenerator> keyGenerator() default DefaultKeyGenerator.class;

    Class<? extends CacheCondition> condition() default DefaultCacheCondition.class;
}
