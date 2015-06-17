package com.diwayou.zkm.config;

import com.diwayou.zkm.exception.ClusterAlreadyExistException;
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
    public void addCluster(String clusterName) {
        List<InetSocketAddress> oldValue = config.putIfAbsent(clusterName, Lists.<InetSocketAddress>newArrayList());

        if (oldValue != null) {
            throw new ClusterAlreadyExistException("clusterName exists already.");
        }
    }

    @Override
    public void addServer(String clusterName, String serverIp, int clientPort) {
        List<InetSocketAddress> addressList = config.get(clusterName);

        if (addressList == null) {
            throw new RuntimeException("You should addCluster first.");
        }

        addressList.add(new InetSocketAddress(serverIp, clientPort));
    }

    @Override
    public void addServer(String clusterName, String connectString) {
        List<InetSocketAddress> addressList = config.get(clusterName);

        if (addressList == null) {
            throw new RuntimeException("You should addCluster first.");
        }

        ConnectStringParser parser = new ConnectStringParser(connectString);

        addressList.addAll(parser.getServerAddresses());
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
