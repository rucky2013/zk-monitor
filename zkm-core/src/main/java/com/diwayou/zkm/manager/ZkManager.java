package com.diwayou.zkm.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by cn40387 on 15/7/21.
 */
@Component("zkManager")
public class ZkManager {

    private static final Logger logger = LoggerFactory.getLogger(ZkManager.class);

    private final ConcurrentMap<String, CuratorFramework> clusterClient = Maps.newConcurrentMap();

    public void addCluster(String clusterName, String connString) {
        deleteCluster(clusterName);

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connString, retryPolicy);
        client.start();

        CuratorFramework oldClient = clusterClient.putIfAbsent(clusterName, client);
        if (oldClient != null) {
            client.close();
        }
    }

    public void deleteCluster(String clusterName) {
        CuratorFramework oldClient = clusterClient.get(clusterName);
        if (oldClient != null) {
            oldClient.close();
            clusterClient.remove(clusterName);
        }
    }

    public CuratorFramework getClient(String clusterName) {
        return clusterClient.get(clusterName);
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
