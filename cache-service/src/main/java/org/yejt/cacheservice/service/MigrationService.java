package org.yejt.cacheservice.service;

import com.netflix.appinfo.InstanceInfo;

import java.util.Map;

public interface MigrationService<K, V>
{
    public void migrateTo(Map<K, V> data, InstanceInfo server); //TO

    public void migrateFrom(Map<K, V> data); // FROM
}
