package org.yejt.cacheclient.annotation;

import org.yejt.cacheclient.codec.CacheCodec;
import org.yejt.cacheclient.codec.DefaultCacheCodec;
import org.yejt.cacheclient.condition.CacheCondition;
import org.yejt.cacheclient.condition.DefaultCacheCondition;
import org.yejt.cacheclient.keygen.DefaultKeyGenerator;
import org.yejt.cacheclient.keygen.KeyGenerator;

import java.lang.annotation.*;

/**
 * @author keys961
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CachePut {
    String cacheName();

    Class<? extends KeyGenerator> keyGenerator() default DefaultKeyGenerator.class;

    Class<? extends CacheCondition> condition() default DefaultCacheCondition.class;

    Class<? extends CacheCodec> codec() default DefaultCacheCodec.class;
}
