package org.yejt.cacheservice.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author keys961
 */
@Component("cacheManagerProperties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "xxcache")
public class CacheManagerProperties {
    private String name;

    private List<CacheProperties> caches;
}
