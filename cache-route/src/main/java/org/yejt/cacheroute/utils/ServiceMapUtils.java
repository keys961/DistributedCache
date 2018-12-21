package org.yejt.cacheroute.utils;

import com.netflix.appinfo.InstanceInfo;

import java.util.*;

public class ServiceMapUtils
{
    private static TreeMap<Integer, InstanceInfo> serverTreeMap
            = new TreeMap<>();

    private static Map<InstanceInfo, Long> serverTimestamp = new HashMap<>();

    private static Set<InstanceInfo> serverSet = new HashSet<>();

    // TODO: Need to persist that...
    private static long version = -1L;

    public static TreeMap<Integer, InstanceInfo> getServerTreeMap()
    {
        return serverTreeMap;
    }

    public static void setServerTreeMap(TreeMap<Integer, InstanceInfo> serverTreeMap)
    {
        ServiceMapUtils.serverTreeMap = serverTreeMap;
    }

    public static synchronized void addServer(Collection<Integer> hashVals,
                                              InstanceInfo server, Long timestamp)
    {
        if(!serverTimestamp.containsKey(server) || serverTimestamp.get(server) < timestamp)
        {
            hashVals.forEach(i -> serverTreeMap.put(i, server)); // put hash val
            serverTimestamp.put(server, timestamp); // set timestamp/id
            serverSet.add(server); // add server
            version++;
        }
    }

    public static synchronized void removeServer(InstanceInfo server, Long timestamp)
    {
        if(!serverTimestamp.containsKey(server) || timestamp > serverTimestamp.get(server))
        {
            serverTreeMap.entrySet().removeIf(entry -> entry.getValue().equals(server)); //remove map
            serverTimestamp.put(server, timestamp); // update timestamp
            serverSet.remove(server); // remove server set
            version++;
        }
    }

    @Deprecated
    public static synchronized void removeServer(Map<InstanceInfo, Long> serverSet)
    {
        serverSet.forEach(ServiceMapUtils::removeServer);
    }

    public static synchronized void removeServerForcely(Set<InstanceInfo> set)
    {
        set.forEach(server ->
        {
            serverTreeMap.keySet().
                    removeIf(hashVal -> serverTreeMap.get(hashVal).equals(server));
            serverSet.remove(server);
            // without update timestamp
            version++;
        });
    }

    public static Set<InstanceInfo> getServerSet()
    {
        return serverSet;
    }

    public static long getVersion()
    {
        return version;
    }
}
