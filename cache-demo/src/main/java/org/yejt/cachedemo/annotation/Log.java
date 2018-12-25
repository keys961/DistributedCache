package org.yejt.cachedemo.annotation;

import java.lang.annotation.*;

/**
 * @author keys961
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Log {
}
