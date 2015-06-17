package com.diwayou.zkm.controller;

import com.diwayou.zkm.controller.handler.BodyRenderHandler;
import com.diwayou.zkm.manager.MonitorManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by cn40387 on 15/6/16.
 */
@Controller
@RequestMapping("/cluster")
public class ClusterController extends AbstractController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(HttpServletRequest request, ModelMap map, @RequestParam(value = "comm", required = false) final String comm) {
        return renderBody(map, request, new BodyRenderHandler() {
            @Override
            public void handle(ModelMap map) {
                String content = null;
                String command = StringUtils.isEmpty(comm) ? "envi" : comm;
                try {
                    content = MonitorManager.sendCommand("10.101.58.51", 2181, command);
                } catch (IOException e) {
                    putAlertMsg(map, e);
                }

                map.put("content", content.replace("\n", "<br/>"));
            }
        });
    }

    @Override
    protected String getFlag() {
        return "cluster_flag";
    }
}
