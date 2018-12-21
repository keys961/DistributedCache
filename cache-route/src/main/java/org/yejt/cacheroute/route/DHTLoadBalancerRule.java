package org.yejt.cacheroute.route;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yejt.cacheroute.utils.ConsistentHash;
import org.yejt.cacheroute.utils.ServiceMapUtils;

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

    @Override
    public Server choose(Object o)
    {
        LOGGER.info("Load balancer key: {}.", o);
        if(o == null)
            return null;

        int hashVal = ConsistentHash.getHash(o.toString());

        List<Server> serverList = iLoadBalancer.getReachableServers();
        if(serverList.isEmpty())
            return null;

        InstanceInfo properties =
                getServer(ServiceMapUtils.getServerTreeMap(), hashVal);

        if(properties == null)
            return null;

        final Server[] result = {null};
        serverList.forEach(server ->
            {
                InstanceInfo info = ((DiscoveryEnabledServer)server).getInstanceInfo();
                if(properties.getAppName().equals(info.getAppName())
                    && Integer.parseInt(properties.getInstanceId().split(":")[2])
                        == info.getPort() && properties.getIPAddr().equals(info.getIPAddr()))
                    result[0] = server;
            });
        if(!(result[0].isAlive() && result[0].isReadyToServe()))
            result[0] = null;
        else
            LOGGER.info("Server chosen: {}", result[0]);
        return result[0];
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

    private InstanceInfo
        getServer(TreeMap<Integer, InstanceInfo> serverMap, int hashVal)
    {
        Integer ceilKey = serverMap.ceilingKey(hashVal);
        if(ceilKey == null)
            ceilKey = serverMap.firstKey();
        return serverMap.get(ceilKey);
    }
}
