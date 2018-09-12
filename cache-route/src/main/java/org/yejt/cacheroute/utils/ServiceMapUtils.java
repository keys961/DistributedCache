package org.yejt.cacheroute.utils;

import com.netflix.loadbalancer.Server;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class ServiceMapUtils
{
    private static TreeMap<Integer, Server> serverTreeMap = new TreeMap<>();

    private static Set<Server> serverSet = new HashSet<>();

    public static TreeMap<Integer, Server> getServerTreeMap()
    {
        return serverTreeMap;
    }

    public static void setServerTreeMap(TreeMap<Integer, Server> serverTreeMap)
    {
        ServiceMapUtils.serverTreeMap = serverTreeMap;
    }

    public static synchronized void addServer(Collection<Integer> hashVals, Server server)
    {
        hashVals.forEach(i -> serverTreeMap.put(i, server));
        serverSet.add(server);
    }

    public static synchronized void removeServer(Server server)
    {
        serverTreeMap.forEach((i, s) ->
            {
                if(s.equals(server))
                    serverTreeMap.remove(i);
            });
        serverSet.remove(server);
    }

    public static Set<Server> getServerSet()
    {
        return serverSet;
    }
}
