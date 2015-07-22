package com.diwayou.zkm.controller;

import com.diwayou.zkm.controller.handler.BodyRenderHandler;
import com.diwayou.zkm.manager.MonitorManager;
import com.diwayou.zkm.net.COMMAND;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by cn40387 on 15/6/16.
 */
@Controller
@RequestMapping("/command")
public class CommandController extends AbstractController {

    @Autowired
    private MonitorManager monitorManager;

    @RequestMapping(value = "/exec", method = RequestMethod.GET)
    public String exec(HttpServletRequest request, ModelMap map,
                       @RequestParam("cname") final String clusterName,
                       @RequestParam(value = "command", required = false) final String command) {
        return renderBody(map, request, new BodyRenderHandler() {
            @Override
            public void handle(ModelMap map) {
                Map<String, String> statusMap = Maps.newHashMap();
                try {
                    if (!StringUtils.isEmpty(command)) {
                        statusMap = monitorManager.sendCommand(clusterName, command);
                    }

                } catch (Exception e) {
                    putAlertMsg(map, e);
                }

                List<String> commands = Lists.newArrayList();
                for (COMMAND comm : COMMAND.values()) {
                    commands.add(comm.getComm());
                }

                map.put("commands", commands);
                map.put("cname", clusterName);
                map.put("statusMap", statusMap);
            }
        });
    }

    @Override
    protected String getFlag() {
        return "command_flag";
    }
}
