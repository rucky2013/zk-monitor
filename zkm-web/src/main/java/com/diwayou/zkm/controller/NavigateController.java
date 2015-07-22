package com.diwayou.zkm.controller;

import com.alibaba.fastjson.JSON;
import com.diwayou.zkm.controller.handler.BodyRenderHandler;
import com.diwayou.zkm.manager.ZkManager;
import com.diwayou.zkm.vm.TreeNode;
import com.google.common.collect.Lists;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cn40387 on 15/6/16.
 */
@Controller
@RequestMapping("/nav")
public class NavigateController extends AbstractController {

    private static final String NO_DATA = "No data";

    @Autowired
    private ZkManager zkManager;

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public String tree(HttpServletRequest request, ModelMap map, @RequestParam("cname") final String clusterName) {
        return renderBody(map, request, new BodyRenderHandler() {
            @Override
            public void handle(ModelMap map) {
                List<String> nodeList = zkManager.tree(clusterName, "/");

                List<TreeNode> treeNodes = Lists.newArrayListWithCapacity(nodeList.size());
                for (String node : nodeList) {
                    treeNodes.add(new TreeNode(node, Arrays.asList(new TreeNode(NO_DATA))));
                }

                map.put("treeNodes", JSON.toJSONString(treeNodes));
                map.put("cname", clusterName);
            }
        });
    }

    @RequestMapping(value = "/nodeTree", method = RequestMethod.GET)
    @ResponseBody
    public String nodeTree(HttpServletRequest request, ModelMap map,
                           @RequestParam("cname") final String clusterName,
                           @RequestParam("path") String path) {
        try {
            List<String> nodeList = zkManager.tree(clusterName, path);

            List<TreeNode> treeNodes = Lists.newArrayListWithCapacity(nodeList.size());
            for (String node : nodeList) {
                treeNodes.add(new TreeNode(node, Arrays.asList(new TreeNode(NO_DATA))));
            }

            return JSON.toJSONString(treeNodes);
        } catch (Exception e) {
            LOG.error("nodeTree error: clusterName={}, path={}", clusterName, path, e);

            return "error";
        }
    }

    @RequestMapping(value = "/node", method = RequestMethod.GET)
    public String node(HttpServletRequest request, ModelMap map, @RequestParam("cname") final String clusterName,
                         @RequestParam("path") final String path) throws UnsupportedEncodingException {

        return renderBody(map, request, new BodyRenderHandler() {
            @Override
            public void handle(ModelMap map) {
                byte[] data = zkManager.nodeValue(clusterName, path);

                String content = null;
                if (!ArrayUtils.isEmpty(data)) {
                    content = new String(data);
                }

                if (StringUtils.isEmpty(content)) {
                    content = "No Data";
                }
                map.put("content", content);
            }
        }, HTML);
    }

    @Override
    protected String getFlag() {
        return "navigate_flag";
    }
}
