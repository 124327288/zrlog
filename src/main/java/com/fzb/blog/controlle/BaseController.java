package com.fzb.blog.controlle;

import com.fzb.blog.model.*;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.CacheName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BaseController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    private String templatePath;

    private Integer rows;

    private Map<String, Object> webSite;

    @Before({CacheInterceptor.class})
    @CacheName("/post/initData")
    public void initData() {
        Map<String, Object> init = CacheKit.get("/post/initData", "initData");
        if (init == null) {
            init = new HashMap<String, Object>();
            init.put("webSite", WebSite.dao.getWebSite());
            init.put("links", Link.dao.queryAll());
            init.put("types", Type.dao.queryAll());
            init.put(
                    "logNavs",
                    LogNav.dao.queryAll(getRequest().getScheme() + "://"
                            + getRequest().getHeader("host")
                            + getRequest().getContextPath()));
            init.put("plugins", Plugin.dao.queryAll());
            init.put("archives", Log.dao.getArchives());
            init.put("tags", Tag.dao.queryAll());
            init.put("hotLog", Log.dao.getLogsByPage(1, 6));
            List<Type> types = Type.dao.queryAll();
            Map<Map<String, Object>, List<Log>> indexHotLog = new LinkedHashMap<Map<String, Object>, List<Log>>();
            for (Type type : types) {
                Map<String, Object> typeMap = new TreeMap<String, Object>();
                typeMap.put("typeName", type.getStr("typeName"));
                typeMap.put("alias", type.getStr("alias"));
                indexHotLog.put(
                        typeMap,
                        (List<Log>) Log.dao.getLogsBySort(1, 6,
                                type.getStr("alias")).get("rows"));
            }
            init.put("indexHotLog", indexHotLog);
            CacheKit.put("/post/initData", "initData", init);
            JFinal.me().getServletContext()
                    .setAttribute("webSite", init.get("webSite"));
        }
        setAttr("init", init);
        this.templatePath = ((Map<String, Object>) init.get("webSite")).get(
                "template").toString();
        this.rows = Integer
                .parseInt(((Map<String, Object>) init.get("webSite")).get(
                        "rows").toString());

        //
        webSite = (Map<String, Object>) init.get("webSite");
    }

    public String getTemplatePath() {
        return this.templatePath;
    }

    public Integer getDefaultRows() {
        return this.rows;
    }

    public Object getValueByKey(String key) {
        if (webSite.get(key) != null) {
            return webSite.get(key).toString();
        }
        return null;
    }

    public String getStrValueByKey(String key) {
        if (webSite.get(key) != null) {
            return webSite.get(key).toString();
        }
        return null;
    }

    public static void refreshCache() {
        CacheKit.remove("/post/initData", "initData");
    }

    public boolean isNotNullOrNotEmptyStr(Object... args) {
        for (Object arg : args) {
            if (arg == null || "".equals(arg)) {
                return false;
            }
        }
        return true;
    }

    public boolean getStaticHtmlStatus() {
        Object obj = getStrValueByKey("pseudo_staticStatus");
        if (obj != null) {
            return "on".equals(obj.toString());
        }
        return false;
    }
}
