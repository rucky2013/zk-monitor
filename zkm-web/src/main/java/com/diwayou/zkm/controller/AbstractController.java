package com.diwayou.zkm.controller;

import com.diwayou.zkm.controller.handler.BodyRenderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by cn40387 on 15/6/16.
 */
public abstract class AbstractController {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected abstract String getFlag();

    public static final String TITLE = "title";

    public static final String BODY_PAGE = "bodyPage";

    public static final String TEMPLATE = "template";

    public static final String COMMON_TITLE = "Zookeeper Cluster Monitor";

    public static final String SUCCESS = "success";

    public static final String HTML = "html_template";

    protected String renderBody(ModelMap map, HttpServletRequest request, BodyRenderHandler handler, String template) {
        try {
            putPublicAttribute(map, request);

            if (handler != null) {
                handler.handle(map);
            }
        } catch (Exception e) {
            LOG.error("renderBody error: path=" + request.getServletPath(), e);

            putAlertMsg(map, e);
        }

        return template;
    }

    protected String renderBody(ModelMap map, HttpServletRequest request, BodyRenderHandler handler) {
        return renderBody(map, request, handler, TEMPLATE);
    }

    protected String renderBody(ModelMap map, HttpServletRequest request) {
        return renderBody(map, request, null);
    }

    protected void putPublicAttribute(ModelMap map, HttpServletRequest request) {
        map.put(getFlag(), "active");
        map.put(TITLE, COMMON_TITLE);
        map.put(BODY_PAGE, request.getServletPath() + ".vm");
    }

    protected void putAlertMsg(ModelMap map, Throwable t) {
        map.put("alertMsg", t.getMessage());
    }

    protected void putAlertMsg(ModelMap map, String msg) {
        map.put("alertTrue", true);
        map.put("msg", msg);
    }
}
