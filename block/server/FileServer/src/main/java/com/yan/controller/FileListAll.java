package com.yan.controller;

import com.yan.dao.IMyFileDao;
import com.yan.dao.impl.MyFileImpl;
import com.yan.domain.MyFile;
import com.yan.utils.PermissionCheck;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileListAll extends HttpServlet {
    private static Logger logger = Logger.getLogger(FileListAll.class);
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        InputStream in = null;
        OutputStream out = null;
        try{
        String sid = req.getHeader("X-SID");
        System.out.println("原始X-SID:" + sid);
        String signature = req.getHeader("X-Signature");
        System.out.println("原始X-Signature:" + signature);
        String[] sigarr = signature.split("  ");
        String sig2sid = sigarr[0] + "\n" + sigarr[1] + "\n" + sigarr[2];
        String decSID = PermissionCheck.Signature2SID(sig2sid);
        System.out.println("解密后X-SID:" + decSID);
        if (!decSID.equals(sid)) {
            logger.error("权限错误");
            resp.setStatus(403);
            return;
        }
        IMyFileDao dao = new MyFileImpl();
        List<MyFile> myFileList = new ArrayList<>();
        myFileList = dao.listFile();
        List<String> strList = new ArrayList<>();

        for (MyFile myFile : myFileList) {
            System.out.println(myFile);
            strList.add(myFile.toString());
        }
        String jsonStr = String.join(",", strList);
        System.out.println(jsonStr);
        int readLength = 0;



        out = resp.getOutputStream();
        OutputStreamWriter outwriter = new OutputStreamWriter(out, "UTF-8");
        outwriter.write(jsonStr);
        outwriter.flush();
       } catch (Exception e) {
            logger.error(e.getMessage());
            resp.setStatus(404);
            e.printStackTrace();
            return;
       }
        finally {
           out.close();

       }
    }
}
