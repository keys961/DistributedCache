package org.yejt.cacheclient.annotation;

import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author keys961
 */
@Configuration
@ComponentScan(basePackages = "org.yejt.cacheclient",
        excludeFilters =
                {
                        @ComponentScan.Filter(type = FilterType.CUSTOM,
                                classes = {TypeExcludeFilter.class}),
                        @ComponentScan.Filter(type = FilterType.CUSTOM,
                                classes = {AutoConfigurationExcludeFilter.class})
                })
public class XXCacheAutoConfig {

}
