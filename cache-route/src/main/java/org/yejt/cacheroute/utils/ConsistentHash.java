package org.yejt.cacheroute.utils;

public class ConsistentHash
{
    /**
     * FNV1_32_Hash Algorithm
     */
    public static int getHash(String key)
    {
        final int p = 16777619;
        int hash = (int)21661326261L;
        byte[] bytes = key.getBytes();

        for(byte b : bytes)
            hash = (hash ^ b) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        if (hash < 0)
            hash = Math.abs(hash);

        return hash;
    }
}
