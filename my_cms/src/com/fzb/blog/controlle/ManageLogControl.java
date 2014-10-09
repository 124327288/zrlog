package com.fzb.blog.controlle;

import com.fzb.blog.model.Log;
import com.fzb.blog.model.Tag;
import com.fzb.blog.model.User;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;

public class ManageLogControl extends ManageControl
{
  public void list()
  {
     renderJson(Log.dao.getLogsByPage(getParaToInt("page").intValue(), getParaToInt("rows").intValue()));
  }

  public void write() {
     render("/admin/edit.jsp");
  }

  public void oper() {
     if ((getPara("oper") != null) && 
       (getPara("oper").equals("del"))) {
       Map log = Log.dao.getLogByLogId(getParaToInt("id").intValue());
       Tag.dao.deleteTag(log.get("keywords").toString());
       Log.dao.deleteById(getParaToInt("id"));
    }

     renderJson("OK");
  }
  public void edit_frame(){
	  Map log = new HashMap();
     Integer logId = null;
     try {
       logId = getParaToInt(0);
     }
     catch (NumberFormatException e) {
       logId = Integer.valueOf(Log.dao.getLogByLogIdAlias(getPara(0)));
     }
     log.putAll(Log.dao.getLogByLogId(logId.intValue()));
     log.put("lastLog", Log.dao.getLastLog(logId.intValue()));
     log.put("nextLog", Log.dao.getNextLog(logId.intValue()));
     setAttr("log", log);
	  render("/admin/edit_frame.jsp");
  }

  public void add() {
     Map<String,String[]> param = getRequest().getParameterMap();
     Log log = new Log();
     for (Map.Entry tmap : param.entrySet()) {
       log.set((String)tmap.getKey(), ((String[])tmap.getValue())[0]);
    }
     int logId = log.getMaxRecord() + 1;
     ((Log)log.set("userId", ((User)getSessionAttr("user")).getInt("userId"))).set("releaseTime", new Date());
     log.set("logId", Integer.valueOf(logId));

     if (param.get("alias") == null) {
       log.set("alias", Integer.valueOf(logId));
    }
     if (param.get("canComment") != null) {
       log.set("canComment", Boolean.valueOf(true));
    }
    else {
       log.set("canComment", Boolean.valueOf(false));
    }
     if (param.get("recommended") != null) {
       log.set("recommended", Boolean.valueOf(true));
    }
    else {
       log.set("recommended", Boolean.valueOf(false));
    }
     if (param.get("digest") == null) {
       log.set("digest", log.get("content"));
    }

     Object map = new HashMap();
     ((Map)map).put("add", Boolean.valueOf(log.save()));
     renderJson(map);
  }

  public void upload() {
     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
     String fileExt = getFile("imgFile").getFileName().substring(getFile("imgFile").getFileName().lastIndexOf(".") + 1).toLowerCase();
     SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
     String url = "/attached/" + sdf.format(new Date()) + "/" + df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
    try {
       FileUtils.moveFile(getFile("imgFile").getFile().getAbsoluteFile(), new File(PathKit.getWebRootPath() + url));
       getData().put("error", Integer.valueOf(0));
       getData().put("url", getRequest().getContextPath() + url);
    } catch (IOException e) {
       getData().put("error", "上传失败了");
       e.printStackTrace();
    }
     renderJson(getData());
  }
}
