package com.diwayou.zkm.controller;

import com.diwayou.zkm.controller.handler.BodyRenderHandler;
import com.diwayou.zkm.manager.MonitorManager;
import com.diwayou.zkm.net.COMMAND;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by cn40387 on 15/6/16.
 */
@Controller
@RequestMapping("/cluster")
public class ClusterController extends AbstractController {

    @Autowired
    private MonitorManager monitorManager;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(HttpServletRequest request, ModelMap map) {
        return renderBody(map, request, new BodyRenderHandler() {
            @Override
            public void handle(ModelMap map) {
                map.put("clusterNames", monitorManager.getClusterList());
            }
        });
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String list(HttpServletRequest request, ModelMap map, @RequestParam("cname") final String clusterName) {
        return renderBody(map, request, new BodyRenderHandler() {
            @Override
            public void handle(ModelMap map) {
                String command = COMMAND.MNTR.getComm();

                Map<String, String> statusMap = Maps.newHashMap();
                try {
                    statusMap = monitorManager.sendCommand(clusterName, command);

                } catch (Exception e) {
                    putAlertMsg(map, e);
                }

                map.put("statusMap", statusMap);
            }
        });
    }

    @Override
    protected String getFlag() {
        return "cluster_flag";
    }
}
