package org.yejt.cacheservice.service;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Migration service
 *
 * @param <K>: Key type
 * @param <V>: Value type
 * @author keys961
 */
@Service
public class XXMigrateService<K, V> implements MigrationService<K, V> {

    @Override
    public void migrateTo(Map<K, V> data, InstanceInfo server) {

    }

    @Override
    public void migrateFrom(Map<K, V> data) {

    }
}
