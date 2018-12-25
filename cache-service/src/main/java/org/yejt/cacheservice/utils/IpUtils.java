package org.yejt.cacheservice.utils;

import java.net.InetAddress;

/**
 * @author keys961
 */
public class IpUtils {
    public static String getLocalHostIP() {
        String ip;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
        } catch (Exception ex) {
            ip = "";
        }

        return ip;
    }

    public static int getPort(String instanceId) {
        if (instanceId == null)
            return 0;
        return Integer.parseInt(instanceId.split(":")[2]);
    }
}
