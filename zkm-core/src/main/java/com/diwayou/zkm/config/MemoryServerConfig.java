package com.diwayou.zkm.config;

import com.google.common.collect.Lists;
import org.apache.zookeeper.client.ConnectStringParser;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by cn40387 on 15/6/16.
 */
public class MemoryServerConfig implements ServerConfig {

    private ConcurrentMap<String/*clusterName*/, String> config = new ConcurrentHashMap<String, String>();

    @Override
    public void addCluster(String clusterName, String connectString) {
        config.put(clusterName, connectString);
    }

    @Override
    public void deleteCluster(String clusterName) {
        config.remove(clusterName);
    }

    @Override
    public Collection<String> getClusterNames() {
        return config.keySet();
    }

    @Override
    public String getConnectString(String clusterName) {
        return config.get(clusterName);
    }
}
