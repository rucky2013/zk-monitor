package com.diwayou.zkm.manager;

import com.diwayou.zkm.config.MemoryServerConfig;
import com.diwayou.zkm.config.ServerConfig;
import com.diwayou.zkm.net.NetUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by cn40387 on 15/6/16.
 */
@Component("monitorManager")
public class MonitorManager {

    @Autowired
    private ZkManager zkManager;

    public static final String ZK_NO_RESPONSE = "May Down";

    private ServerConfig serverConfig = new MemoryServerConfig();

    public Collection<String> getClusterList() {
        return serverConfig.getClusterNames();
    }

    public void addCluster(String clusterName, String connString) {
        serverConfig.addCluster(clusterName, connString);
        zkManager.addCluster(clusterName, connString);
    }

    public void deleteCluster(String clusterName) {
        serverConfig.deleteCluster(clusterName);
        zkManager.deleteCluster(clusterName);
    }

    public List<InetSocketAddress> getAddresses(String clusterName) {
        return serverConfig.getServerAddresses(clusterName);
    }

    public String sendCommand(String serverIp, int clientPort, String command) throws IOException {
        return NetUtil.sendCommand(serverIp, clientPort, command);
    }

    public Map<String, String> sendCommand(String clusterName, String command) throws IOException {
        List<InetSocketAddress> addresses = serverConfig.getServerAddresses(clusterName);

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
}
