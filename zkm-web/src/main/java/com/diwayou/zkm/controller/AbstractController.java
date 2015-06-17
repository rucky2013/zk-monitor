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

    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

    protected abstract String getFlag();

    public static final String TITLE = "title";

    public static final String BODY_PAGE = "bodyPage";

    public static final String TEMPLATE = "template";

    public static final String COMMON_TITLE = "Event Platform Admin";

    protected String renderBody(ModelMap map, HttpServletRequest request, BodyRenderHandler handler) {
        try {
            putPublicAttribute(map, request);

            handler.handle(map);
        } catch (Exception e) {
            LOG.error("renderBody error: path=" + request.getServletPath(), e);

            putAlertMsg(map, e);
        }

        return TEMPLATE;
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
