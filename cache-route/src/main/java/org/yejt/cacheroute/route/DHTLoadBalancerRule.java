package org.yejt.cacheroute.route;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheroute.utils.ConsistentHash;

import java.util.List;
import java.util.TreeMap;

public class DHTLoadBalancerRule implements LoadBalancerRule
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DHTLoadBalancerRule.class);

    private int virtualNodeCount;

    private ILoadBalancer iLoadBalancer;

    public DHTLoadBalancerRule()
    {
        this.virtualNodeCount = 1;
    }

    public DHTLoadBalancerRule(int virtualNodeCount)
    {
        this.virtualNodeCount = 1;
        if(virtualNodeCount > 0)
            this.virtualNodeCount = virtualNodeCount;
    }

    /**
     * TODO: Server list is global, updated by another thread/schedule
     */
    @Override
    public Server choose(Object o)
    {
        LOGGER.info("Load balancer key: {}.", o);
        LOGGER.info("Virtual nodes count: {}", virtualNodeCount);
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
