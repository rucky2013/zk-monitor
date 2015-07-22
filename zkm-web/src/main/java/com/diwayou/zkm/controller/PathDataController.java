package com.diwayou.zkm.controller;

import com.diwayou.zkm.controller.handler.BodyRenderHandler;
import com.diwayou.zkm.manager.ZkManager;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
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
@RequestMapping("/path")
public class PathDataController extends AbstractController {

    @Autowired
    private ZkManager zkManager;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(HttpServletRequest request, ModelMap map,
                         @RequestParam("cname") String clusterName,
                         @RequestParam("path") String path,
                         @RequestParam("data") String data,
                         @RequestParam("cm") String createMode) throws UnsupportedEncodingException {
        path = StringUtils.trim(path);

        zkManager.createPath(clusterName, path, data, createMode);

        return "redirect:/path/createOrUpdate?c=true&cname=" + URLEncoder.encode(clusterName, "UTF-8") +
                "&path=" + path;
    }

    @RequestMapping(value = "/createOrUpdate", method = RequestMethod.GET)
    public String node(HttpServletRequest request, ModelMap map,
                       @RequestParam(value = "cname", required = false) final String clusterName,
                       @RequestParam(value = "path", required = false) final String path,
                       @RequestParam(value = "c", required = false) final String isCreate) {
        return renderBody(map, request, new BodyRenderHandler() {
            @Override
            public void handle(ModelMap map) {
                try {
                    if (!StringUtils.isEmpty(path)) {
                        byte[] data = zkManager.nodeValue(clusterName, path);
                        if (!ArrayUtils.isEmpty(data)) {
                            map.put("data", new String(data));
                            if (!StringUtils.isEmpty(isCreate)) {
                                putAlertMsg(map, SUCCESS);
                            }
                        }

                        Stat stat = zkManager.nodeStat(clusterName, path);
                        if (stat != null) {
                            if (stat.getEphemeralOwner() != 0) {
                                map.put("createMode", CreateMode.EPHEMERAL);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOG.error("/path/createOrUpdate error: clusterName={}, path={}", clusterName, path, e);
                }

                map.put("cms", CreateMode.values());
                map.put("cname", clusterName);
                map.put("path", path);
            }
        });
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String get(HttpServletRequest request, ModelMap map) throws UnsupportedEncodingException {
        return renderBody(map, request);
    }

    @RequestMapping(value = "/deleteForm", method = RequestMethod.GET)
    public String deleteForm(HttpServletRequest request, ModelMap map) throws UnsupportedEncodingException {
        return renderBody(map, request);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(HttpServletRequest request, ModelMap map,
                         @RequestParam("cname") String clusterName,
                         @RequestParam("path") String path) throws UnsupportedEncodingException {
        path = StringUtils.trim(path);

        zkManager.deletePath(clusterName, path);

        return "redirect:/path/createOrUpdate?cname=" + URLEncoder.encode(clusterName, "UTF-8") +
                "&path=" + path;
    }

    @Override
    protected String getFlag() {
        return "path_data_flag";
    }
}
