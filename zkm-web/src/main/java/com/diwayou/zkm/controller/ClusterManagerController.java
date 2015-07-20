package com.diwayou.zkm.controller;

import com.diwayou.zkm.controller.handler.BodyRenderHandler;
import com.diwayou.zkm.manager.MonitorManager;
import com.diwayou.zkm.net.COMMAND;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by cn40387 on 15/6/16.
 */
@Controller
@RequestMapping("/cluster/manager")
public class ClusterManagerController extends AbstractController {

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String list(HttpServletRequest request, ModelMap map, @RequestParam("cname") final String clusterName) {
        return renderBody(map, request, new BodyRenderHandler() {
            @Override
            public void handle(ModelMap map) {
                String command = COMMAND.MNTR.name().toLowerCase();

                Map<String, String> statusMap = Maps.newHashMap();
                try {
                    List<InetSocketAddress> addresses = MonitorManager.getAddresses(clusterName);


                    for (InetSocketAddress socketAddress : addresses) {
                        String content = null;
                        try {
                            content = MonitorManager.sendCommand(socketAddress.getHostString(), socketAddress.getPort(), command);

                            if (!StringUtils.isEmpty(content)) {
                                content.replace("\n", "<br/>");
                            }
                        } catch (Exception e) {
                            content = MonitorManager.ZK_NO_RESPONSE;
                        }
                        statusMap.put(socketAddress.toString(), content);
                    }
                } catch (Exception e) {
                    putAlertMsg(map, e);
                }

                map.put("statusMap", statusMap);
            }
        });
    }

    @RequestMapping(value = "/createForm", method = RequestMethod.GET)
    public String create(HttpServletRequest request, ModelMap map) {
        return renderBody(map, request);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(HttpServletRequest request, ModelMap map, @RequestParam("cname") String clusterName,
                         @RequestParam("connStr") String connString) throws UnsupportedEncodingException {

        MonitorManager.addCluster(clusterName, connString);

        return "redirect:/cluster/manager/detail?cname=" + URLEncoder.encode(clusterName, "UTF-8");
    }

    @Override
    protected String getFlag() {
        return "cluster_manager_flag";
    }
}
