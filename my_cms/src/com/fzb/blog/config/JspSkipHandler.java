 package com.fzb.blog.config;
 
 import com.fzb.common.util.Jsp2Html;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PathKit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
 import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
 import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
 public class JspSkipHandler extends Handler
 {
   public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled)
   {
     if (!target.endsWith(".jsp"))
     {
    	 // 处理静态化文件
    	 if(target.endsWith(".html")){
    		 File htmlFile=new File(PathKit.getWebRootPath() + request.getServletPath());
    		 if(!htmlFile.exists()){
    			 String tempTarget=target.substring(0,target.lastIndexOf("."));
        		 String home=request.getScheme() + "://" + request.getHeader("host")+ request.getContextPath() +tempTarget;
        		 target=tempTarget;
        		 try {
					Jsp2Html.convert2Html(home, htmlFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
    		 }
    		 this.nextHandler.handle(target, request, response, isHandled);
    	 }
    	 else{
    		 this.nextHandler.handle(target, request, response, isHandled);
    	 }
      }
     else
       try {
         request.getSession();
         response.sendRedirect(request.getContextPath() + "/error/403.html");
         return;
       } catch (IOException e) {
         e.printStackTrace();
       }
   }
 }

