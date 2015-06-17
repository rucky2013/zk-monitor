package com.diwayou.zkm.config.util;

import org.apache.zookeeper.client.ConnectStringParser;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by cn40387 on 15/6/16.
 */
public class ServerConfigUtil {

    public String inetAddressesToString(List<InetSocketAddress> addresses) {
        StringBuilder sb = new StringBuilder();

        for (InetSocketAddress address : addresses) {
            sb.append(address.getHostString() + ":" + address.getPort());
            sb.append(",");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        String str = "127.0.0.1:2181,127.0.0.1:2181,127.0.0.1:2181,127.0.0.1:2181,";
        ConnectStringParser parser = new ConnectStringParser(str);
        List<InetSocketAddress> addresses = parser.getServerAddresses();
        System.out.println(addresses);
    }
}
