package org.yejt.cacheroute.utils;

import com.netflix.appinfo.InstanceInfo;

import java.util.*;

/**
 * @author keys961
 */
public class ServiceMapUtils {
    private static TreeMap<Integer, InstanceInfo> serverTreeMap
            = new TreeMap<>();

    private static Map<InstanceInfo, Long> serverTimestamp = new HashMap<>();

    private static Set<InstanceInfo> serverSet = new HashSet<>();

    private static long version = -1L;

    public static TreeMap<Integer, InstanceInfo> getServerTreeMap() {
        return serverTreeMap;
    }

    public static synchronized void setServerTreeMap(TreeMap<Integer, InstanceInfo> serverTreeMap) {
        ServiceMapUtils.serverTreeMap = serverTreeMap;
        ServiceMapUtils.serverSet = new HashSet<>(serverTreeMap.values());
        ServiceMapUtils.serverSet.forEach(info -> {
            if (!serverTimestamp.containsKey(info)) {
                serverTimestamp.put(info, 0L);
            }
        });
        version++;
    }

    public static synchronized void addServer(Collection<Integer> hashVals,
                                              InstanceInfo server, Long timestamp) {
        if (!serverTimestamp.containsKey(server) || serverTimestamp.get(server) < timestamp) {
            // put hash val
            hashVals.forEach(i -> serverTreeMap.put(i, server));
            // set timestamp/id
            serverTimestamp.put(server, timestamp);
            // add server
            serverSet.add(server);
            version++;
        }
    }

    public static synchronized void removeServer(InstanceInfo server, Long timestamp) {
        if (!serverTimestamp.containsKey(server) || timestamp > serverTimestamp.get(server)) {
            //remove map
            serverTreeMap.entrySet().removeIf(entry -> entry.getValue().equals(server));
            // update timestamp
            serverTimestamp.put(server, timestamp);
            // remove server set
            serverSet.remove(server);
            version++;
        }
    }

    public static synchronized void removeServerForcly(InstanceInfo server) {
        Long timestamp = serverTimestamp.get(server);
        if (timestamp == null) {
            timestamp = 0L;
        }
        serverTimestamp.put(server, timestamp);
        serverTreeMap.entrySet().removeIf(entry -> entry.getValue().equals(server));
        serverSet.remove(server);
        version++;
    }

    @Deprecated
    public static synchronized void removeServer(Map<InstanceInfo, Long> serverSet) {
        serverSet.forEach(ServiceMapUtils::removeServer);
    }

    public static Set<InstanceInfo> getServerSet() {
        return serverSet;
    }

    public static long getVersion() {
        return version;
    }
}
