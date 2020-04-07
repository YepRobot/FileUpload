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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Getdetails extends HttpServlet {

    private static Logger logger = Logger.getLogger(Getdetails.class);
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
            String uid=req.getHeader("uid");
            IMyFileDao dao = new MyFileImpl();
            MyFile myFile = new MyFile();
            myFile=dao.getFile(uid);

            String jsonStr = myFile.toString();
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
