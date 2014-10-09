 package com.fzb.common.util;
 
 import com.fzb.blog.model.Log;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.util.Date;
 import java.util.List;
 import org.jdom2.Document;
 import org.jdom2.Element;
 import org.jdom2.output.Format;
 import org.jdom2.output.XMLOutputter;
 import org.quartz.Job;
 import org.quartz.JobDataMap;
 import org.quartz.JobDetail;
 import org.quartz.JobExecutionContext;
 import org.quartz.JobExecutionException;
 
 public class SiteMapJob
   implements Job
 {
   public void execute(JobExecutionContext context)
     throws JobExecutionException
   {
     String webDiskPath = (String)context.getJobDetail().getJobDataMap().get("webDiskPath");
     String webHome = (String)context.getJobDetail().getJobDataMap().get("webHome");
     List<Object[]> logs = Log.dao.getAllAlias();
     Document doc = new Document();
     Element root = new Element("urlset");
     for (Object[] log : logs) {
       Element url = new Element("url");
       Element loc = new Element("loc");
       loc.setText(webHome + "/post/" + log[0].toString());
       url.addContent(loc);
       Element lastmod = new Element("lastmod");
       lastmod.setText(ParseTools.toISO8601((Date)log[1]));
       url.addContent(lastmod);
       Element changefreq = new Element("changefreq");
       changefreq.setText("daily");
       url.addContent(changefreq);
       Element priority = new Element("priority");
       priority.setText("1.0");
       url.addContent(priority);
       root.addContent(url);
     }
     doc.addContent(root);
     XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
     try {
       File file = new File(webDiskPath);
       if (!file.exists()) {
         file.createNewFile();
       }
       FileOutputStream outFile = new FileOutputStream(file);
       out.output(doc, outFile);
       outFile.close();
     } catch (FileNotFoundException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     }
   }
 }

