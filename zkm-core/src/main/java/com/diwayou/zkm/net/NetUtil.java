package com.diwayou.zkm.net;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by cn40387 on 15/6/16.
 */
public class NetUtil {

    public static String sendCommand(String addr, int port, String command) throws IOException {
        Socket socket = new Socket(addr, port);

        OutputStream out = socket.getOutputStream();
        out.write(command.getBytes());
        out.flush();

        return IOUtils.toString(socket.getInputStream());
    }
}
