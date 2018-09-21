package org.yejt.cacheroute.utils;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.Server;
import org.yejt.cacheroute.mq.ClusterNodeReceiver;

import java.util.*;

public class ServiceMapUtils
{
    private static TreeMap<Integer, InstanceInfo> serverTreeMap
            = new TreeMap<>();

    private static Set<InstanceInfo> serverSet = new HashSet<>();

    public static TreeMap<Integer, InstanceInfo> getServerTreeMap()
    {
        return serverTreeMap;
    }

    public static void setServerTreeMap(TreeMap<Integer, InstanceInfo> serverTreeMap)
    {
        ServiceMapUtils.serverTreeMap = serverTreeMap;
    }

    public static synchronized void addServer(Collection<Integer> hashVals,
                                              InstanceInfo server)
    {
        hashVals.forEach(i -> serverTreeMap.put(i, server));
        serverSet.add(server);
    }

    public static synchronized void removeServer(InstanceInfo server)
    {
        serverTreeMap.entrySet().removeIf(entry -> entry.getValue().equals(server));
        serverSet.remove(server);
    }

    public static synchronized void removeServer(Set<InstanceInfo> serverSet)
    {
        ServiceMapUtils.serverSet.removeAll(serverSet);
        serverSet.forEach(ServiceMapUtils::removeServer);
    }

    public static Set<InstanceInfo> getServerSet()
    {
        return serverSet;
    }
}
