package com.diwayou.zkm.config;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

/**
 * Created by cn40387 on 15/6/16.
 */
public interface ServerConfig {

    /**
     * Add a cluster with name clusterName to config, if clusterName exists already will throw
     * ClusterAlreadyExistException
     *
     * @param clusterName unique cluster name per zkm server
     */
    void addCluster(String clusterName);

    /**
     * Add a server to cluster
     *
     * @param clusterName
     * @param serverIp
     * @param clientPort
     */
    void addServer(String clusterName, String serverIp, int clientPort);

    /**
     * Add servers to cluster
     *
     * @param clusterName unique cluster name per zkm server
     * @param connectString e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002"
     */
    void addServer(String clusterName, String connectString);

    Collection<String> getClusterNames();

    List<InetSocketAddress> getServerAddresses(String clusterName);
}
