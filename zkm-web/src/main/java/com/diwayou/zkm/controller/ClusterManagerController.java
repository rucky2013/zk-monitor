package com.diwayou.zkm.controller;

import com.diwayou.zkm.manager.MonitorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by cn40387 on 15/6/16.
 */
@Controller
@RequestMapping("/cluster/manager")
public class ClusterManagerController extends AbstractController {

    @Autowired
    private MonitorManager monitorManager;

    @RequestMapping(value = "/createForm", method = RequestMethod.GET)
    public String create(HttpServletRequest request, ModelMap map) {
        return renderBody(map, request);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(HttpServletRequest request, ModelMap map, @RequestParam("cname") String clusterName,
                         @RequestParam("connStr") String connString) throws UnsupportedEncodingException {

        monitorManager.addCluster(clusterName, connString);

        return "redirect:/cluster/detail?cname=" + URLEncoder.encode(clusterName, "UTF-8");
    }

    @Override
    protected String getFlag() {
        return "cluster_manager_flag";
    }
}
