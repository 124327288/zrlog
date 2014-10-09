 package com.fzb.blog.config;
 
 import com.fzb.blog.controlle.APIControl;
import com.fzb.blog.controlle.InstallControl;
import com.fzb.blog.controlle.QueryLogControl;
import com.fzb.blog.incp.LoginInterceptor;
import com.fzb.blog.model.Comment;
import com.fzb.blog.model.Link;
import com.fzb.blog.model.Log;
import com.fzb.blog.model.LogNav;
import com.fzb.blog.model.Plugin;
import com.fzb.blog.model.Tag;
import com.fzb.blog.model.Type;
import com.fzb.blog.model.User;
import com.fzb.blog.model.WebSite;
import com.fzb.common.util.InstallUtil;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.i18n.I18N;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
 
 public class JFanilConfig extends JFinalConfig
 {
   public void configConstant(Constants con)
   {
     con.setDevMode(true);
     con.setViewType(ViewType.JSP);
     con.setEncoding("utf-8");
     con.setI18n("i18n");
     I18N.init("i18n", null, null);
     con.setError404View("/error/404.html");
     con.setError500View("/error/404.html");
     con.setUploadedFileSaveDirectory(PathKit.getWebRootPath() + "/attached");
   }
 
   public void configHandler(Handlers handlers)
   {
     handlers.add(new JspSkipHandler());
   }
 
   public void configInterceptor(Interceptors incp)
   {
     incp.add(new LoginInterceptor());
   }
 
   public void configPlugin(Plugins plugins)
   {
     loadPropertyFile("db.properties");
     C3p0Plugin c3p0Plugin = new C3p0Plugin(getProperty("jdbcUrl"), 
       getProperty("user"), getProperty("password"));
     try{
    	 if(new InstallUtil(PathKit.getWebRootPath()+"/WEB-INF").checkInstall()){}
    	plugins.add(c3p0Plugin);
    	plugins.add(new EhCachePlugin());
    	 
        plugins.add(new QuartzPlugin());
    
        ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
        plugins.add(arp);
        arp.addMapping("user", "userId", User.class);
        arp.addMapping("log", "logId", Log.class);
        arp.addMapping("type", "typeId", Type.class);
        arp.addMapping("link", "linkId", Link.class);
        arp.addMapping("comment", "commentId", Comment.class);
        arp.addMapping("lognav", "navId", LogNav.class);
        arp.addMapping("website", "siteId", WebSite.class);
        arp.addMapping("plugin", "pluginId", Plugin.class);
        arp.addMapping("tag", "tagId", Tag.class);
     }
     catch(Exception e){
    	 
     }
     
   }
 
   public void configRoute(Routes routes)
   {
     routes.add("/post", QueryLogControl.class);
     routes.add("/api", APIControl.class);
     routes.add("/", QueryLogControl.class);
     
     routes.add("/install", InstallControl.class);
     routes.add(new UserRoutes());
   }
}