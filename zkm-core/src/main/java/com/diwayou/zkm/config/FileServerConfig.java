package com.diwayou.zkm.config;

import org.apache.commons.io.FileUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by cn40387 on 15/6/16.
 */
public class FileServerConfig implements ServerConfig {

    private static final String FILE_NAME = "/config.properties";

    private MemoryServerConfig memoryServerConfig = new MemoryServerConfig();

    private Properties properties = new Properties();

    private String filePath;

    private File file;

    private volatile boolean dirty = false;

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        file = new File(filePath + FILE_NAME);
        try {
            FileUtils.touch(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Reader reader = new FileReader(file)) {
            properties.load(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            memoryServerConfig.addCluster(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (dirty) {
                    try (Writer writer = new FileWriter(file)) {
                        properties.store(writer, null);
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdownNow();
    }

    @Override
    public void addCluster(String clusterName, String connectString) {
        memoryServerConfig.addCluster(clusterName, connectString);

        properties.put(clusterName, connectString);
        dirty = true;
    }

    @Override
    public void deleteCluster(String clusterName) {
        memoryServerConfig.deleteCluster(clusterName);

        properties.remove(clusterName);
        dirty = true;
    }

    @Override
    public Collection<String> getClusterNames() {
        return memoryServerConfig.getClusterNames();
    }

    @Override
    public List<InetSocketAddress> getServerAddresses(String clusterName) {
        return memoryServerConfig.getServerAddresses(clusterName);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
