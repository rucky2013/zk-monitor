package com.diwayou.zkm.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
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
        CuratorFramework oldClient = clusterClient.get(clusterName);
        if (oldClient != null) {
            oldClient.close();
            clusterClient.remove(clusterName);
        }

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connString, retryPolicy);
        client.start();

        oldClient = clusterClient.putIfAbsent(clusterName, client);
        if (oldClient != null) {
            client.close();
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
