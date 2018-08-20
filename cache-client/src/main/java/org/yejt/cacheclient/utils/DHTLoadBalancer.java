package org.yejt.cacheclient.utils;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.*;

public class DHTLoadBalancer implements IRule
{

    private int virtualNodeCount;

    private ILoadBalancer iLoadBalancer;

    public DHTLoadBalancer()
    {
        this.virtualNodeCount = 1;
    }

    @Override
    public Server choose(Object o)
    {
        // TODO: o is null... may be using Zuul
        // o is the K of K-V..
        // it may refined to String...
        System.err.println(o);
        
        if(o == null)
            return null;

        int hashVal = ConsistentHash.getHash(o.toString());


        TreeMap<Integer, Server> serverMap = new TreeMap<>();
        List<Server> serverList = iLoadBalancer.getReachableServers();
        if(serverList.isEmpty())
            return null;

        for(Server server : serverList)
        {
            InstanceInfo info = ((DiscoveryEnabledServer)server).getInstanceInfo();
            String tag = info.getIPAddr() + ":" + info.getPort() + "," + info.getAppName();
            for(int i = 0; i < virtualNodeCount; i++)
            {
                String tTag = tag + i;
                serverMap.put(ConsistentHash.getHash(tTag), server);
            }
        }

        return getServer(serverMap, hashVal);
    }

    @Override
    public ILoadBalancer getLoadBalancer()
    {
        return iLoadBalancer;
    }

    @Override
    public void setLoadBalancer(ILoadBalancer iLoadBalancer)
    {
        this.iLoadBalancer = iLoadBalancer;
    }

    private Server getServer(TreeMap<Integer, Server> serverMap, int hashVal)
    {
        Integer ceilKey = serverMap.ceilingKey(hashVal);
        if(ceilKey == null)
            ceilKey = serverMap.firstKey();

        int cnt = 1;
        while(cnt < serverMap.size() && (!serverMap.get(ceilKey).isAlive() || !serverMap.get(ceilKey).isReadyToServe()))
        {
            cnt++;
            ceilKey = serverMap.higherKey(ceilKey);
            if(ceilKey == null)
                ceilKey = serverMap.firstKey();
        }

        if(serverMap.get(ceilKey).isAlive() && serverMap.get(ceilKey).isReadyToServe())
            return serverMap.get(ceilKey);
        return null;
    }

}
