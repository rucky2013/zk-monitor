package com.diwayou.zkm.manager;

import com.diwayou.zkm.config.ServerConfig;
import com.diwayou.zkm.net.NetUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.client.ConnectStringParser;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by cn40387 on 15/7/21.
 */
@Component("zkManager")
public class ZkManager {

    private static final Logger logger = LoggerFactory.getLogger(ZkManager.class);

    public static final String ZK_NO_RESPONSE = "May Down";

    @Autowired
    private ServerConfig serverConfig;

    private final ConcurrentMap<String, CuratorFramework> clusterClient = Maps.newConcurrentMap();

    public void addCluster(String clusterName, String connString) {
        deleteCluster(clusterName);

        serverConfig.addCluster(clusterName, connString);
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connString, retryPolicy);
        client.start();

        CuratorFramework oldClient = clusterClient.putIfAbsent(clusterName, client);
        if (oldClient != null) {
            client.close();
        }
    }

    public void deleteCluster(String clusterName) {
        serverConfig.deleteCluster(clusterName);

        CuratorFramework oldClient = clusterClient.get(clusterName);
        if (oldClient != null) {
            oldClient.close();
            clusterClient.remove(clusterName);
        }
    }

    public CuratorFramework getClient(String clusterName) {
        CuratorFramework client = clusterClient.get(clusterName);
        if (client == null) {
            String connectStr = serverConfig.getConnectString(clusterName);
            if (!StringUtils.isEmpty(connectStr)) {
                addCluster(clusterName, connectStr);
                client = clusterClient.get(clusterName);
            }
        }

        return client;
    }

    public List<String> tree(String clusterName, String path) {
        try {
            CuratorFramework client = getClient(clusterName);
            if (client == null) {
                return Lists.newArrayList();
            }

            return client.getChildren().forPath(path);
        } catch (Exception e) {
            logger.error("tree error: clusterName={}, path={}", clusterName, path, e);
            return Lists.newArrayList();
        }
    }

    public byte[] nodeValue(String clusterName, String path) {
        try {
            CuratorFramework client = getClient(clusterName);
            if (client == null) {
                return null;
            }

            return client.getData().forPath(path);
        } catch (Exception e) {
            logger.error("nodeValue error: clusterName={}, path={}", clusterName, path, e);
            return null;
        }
    }

    public Stat nodeStat(String clusterName, String path) {
        try {
            CuratorFramework client = getClient(clusterName);
            if (client == null) {
                return null;
            }

            return client.checkExists().forPath(path);
        } catch (Exception e) {
            logger.error("nodeStat error: clusterName={}, path={}", clusterName, path, e);
            return null;
        }
    }

    public void createPath(String clusterName, String path, String data, String createMode) {
        try {
            CuratorFramework client = getClient(clusterName);
            if (client == null) return;

            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.valueOf(createMode))
                    .forPath(path, data.getBytes());
        } catch (Exception e) {
            logger.error("createPath error: clusterName={}, path={}, data={}", clusterName, path, data, e);
        }
    }

    public void deletePath(String clusterName, String path) {
        try {
            CuratorFramework client = getClient(clusterName);
            if (client == null) return;

            client.delete()
                    .deletingChildrenIfNeeded()
                    .forPath(path);
        } catch (Exception e) {
            logger.error("deletePath error: clusterName={}, path={}", clusterName, path, e);
        }
    }

    public Collection<String> getClusterList() {
        return serverConfig.getClusterNames();
    }

    public String sendCommand(String serverIp, int clientPort, String command) throws IOException {
        return NetUtil.sendCommand(serverIp, clientPort, command);
    }

    public Map<String, String> sendCommand(String clusterName, String command) throws IOException {
        String connectStr = serverConfig.getConnectString(clusterName);
        if (StringUtils.isEmpty(connectStr)) {
            return Maps.newHashMap();
        }

        ConnectStringParser parse = new ConnectStringParser(connectStr);
        List<InetSocketAddress> addresses = parse.getServerAddresses();

        Map<String, String> result = Maps.newHashMapWithExpectedSize(addresses.size());
        for (InetSocketAddress address : addresses) {
            String commRe = NetUtil.sendCommand(address.getHostString(), address.getPort(), command);
            if (StringUtils.isEmpty(commRe)) {
                result.put(address.getHostString(), ZK_NO_RESPONSE);
            } else {
                result.put(address.getHostString(), commRe);
            }
        }

        return result;
    }

    @PreDestroy
    public void destroy() {
        for (CuratorFramework client : clusterClient.values()) {
            try {
                client.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
