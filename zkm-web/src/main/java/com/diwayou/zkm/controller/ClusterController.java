package com.diwayou.zkm.controller;

import com.diwayou.zkm.controller.handler.BodyRenderHandler;
import com.diwayou.zkm.manager.MonitorManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by cn40387 on 15/6/16.
 */
@Controller
@RequestMapping("/cluster")
public class ClusterController extends AbstractController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(HttpServletRequest request, ModelMap map) {
        return renderBody(map, request, new BodyRenderHandler() {
            @Override
            public void handle(ModelMap map) {
                map.put("clusterNames", MonitorManager.getClusterList());
            }
        });
    }

    @Override
    protected String getFlag() {
        return "cluster_flag";
    }
}
