package com.yan;

import com.yan.controller.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8091);
        ResourceHandler resource_handler = new ResourceHandler();


        // 配置 ResourceHandler。设置提供文件服务的资源根目录。
        // 在这个例子中是当前目录，也可以配置成jvm有权限访问的任何目录。
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase("src/main/webapp");
        HandlerList handlers = new HandlerList();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        handlers.setHandlers(new Handler[] { resource_handler, context,new DefaultHandler() });
        server.setHandler(handlers);// http://localhost:8080/hello  

        context.addServlet(new ServletHolder(new HelloServlet()), "/hello");// http://localhost:8080/hello/kongxx  
        context.addServlet(new ServletHolder(new UploadServlet()), "/saveFile");
        context.addServlet(new ServletHolder(new Redrec()), "/redirect");
        context.addServlet(new ServletHolder(new DownloadFile()), "/realdwonloadFile");
        context.addServlet(new ServletHolder(new FileListAll()), "/fileListAll");
        context.addServlet(new ServletHolder(new Getdetails()), "/getdetails");
        server.start();
        server.join();
    }
}
