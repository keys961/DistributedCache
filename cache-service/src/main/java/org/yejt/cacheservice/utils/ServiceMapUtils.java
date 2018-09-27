package org.yejt.cacheservice.utils;

import com.netflix.appinfo.InstanceInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class ServiceMapUtils
{
    private static TreeMap<Integer, InstanceInfo> serverTreeMap
            = new TreeMap<>();

    private static Set<InstanceInfo> serverSet = new HashSet<>();

    private static long version = -1L;

    public static TreeMap<Integer, InstanceInfo> getServerTreeMap()
    {
        return serverTreeMap;
    }

    public static synchronized
        void updateServerMap(TreeMap<Integer, InstanceInfo> serverTreeMap,
                             Set<InstanceInfo> serverSet, long version)
    {
        if(ServiceMapUtils.version < version)
        {
            ServiceMapUtils.version = version;
            setServerTreeMap(serverTreeMap);
            setServerSet(serverSet);
        }
        // else DROP
    }

    public static synchronized void setServerTreeMap(TreeMap<Integer, InstanceInfo> serverTreeMap)
    {
        ServiceMapUtils.serverTreeMap = serverTreeMap;
    }

    public static synchronized void setServerSet(Set<InstanceInfo> serverSet)
    {
        ServiceMapUtils.serverSet = serverSet;
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

