package org.yejt.cacheservice.service;

import com.netflix.appinfo.InstanceInfo;

import java.util.Map;

/**
 * Migration service interface
 * TODO: Migration service
 *
 * @param <K>: Key type
 * @param <V>: Value type
 * @author keys961
 */
public interface MigrationService<K, V> {
    /**
     * Migrate data to the other server.
     * @param data: Data to be migrated
     * @param server: Target server
     */
    void migrateTo(Map<K, V> data, InstanceInfo server); //TO

    /**
     * Receive the migrated data into the server
     * @param data: Data migrated from other servers
     */
    void migrateFrom(Map<K, V> data); // FROM
}
