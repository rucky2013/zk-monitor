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

    private ConcurrentMap<String/*clusterName*/, List<InetSocketAddress>> config = new ConcurrentHashMap<String, List<InetSocketAddress>>();

    @Override
    public void addCluster(String clusterName, String connectString) {
        ConnectStringParser parser = new ConnectStringParser(connectString);

        List<InetSocketAddress> addresses = Lists.newArrayList();
        addresses.addAll(parser.getServerAddresses());
        config.put(clusterName, addresses);
    }

    @Override
    public Collection<String> getClusterNames() {
        return config.keySet();
    }

    @Override
    public List<InetSocketAddress> getServerAddresses(String clusterName) {
        return config.get(clusterName);
    }
}
