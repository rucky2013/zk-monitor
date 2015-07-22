package com.diwayou.zkm.config;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

/**
 * Created by cn40387 on 15/6/16.
 */
public interface ServerConfig {

    /**
     * Add servers to cluster
     *
     * @param clusterName unique cluster name per zkm server
     * @param connectString e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002"
     */
    void addCluster(String clusterName, String connectString);

    void deleteCluster(String clusterName);

    Collection<String> getClusterNames();

    List<InetSocketAddress> getServerAddresses(String clusterName);
}
