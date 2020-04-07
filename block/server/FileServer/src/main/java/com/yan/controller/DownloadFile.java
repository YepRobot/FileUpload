package com.yan.controller;

import com.yan.dao.IMyFileDao;
import com.yan.dao.impl.MyFileImpl;
import com.yan.domain.MyFile;
import com.yan.utils.PermissionCheck;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import org.apache.log4j.*;

public class DownloadFile extends HttpServlet {
    private static Logger logger = Logger.getLogger(DownloadFile.class);
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int BUFFER_SIZE = 100000;
        InputStream in = null;
        OutputStream out = null;
        String sid = request.getHeader("X-SID");
        System.out.println("原始X-SID:" + sid);
        String signature = request.getHeader("X-Signature");
        System.out.println("原始X-Signature:" + signature);
        String[] sigarr = signature.split("  ");
        String sig2sid = sigarr[0] + "\n" + sigarr[1] + "\n" + sigarr[2];
        String decSID = PermissionCheck.Signature2SID(sig2sid);
        System.out.println("解密后X-SID:" + decSID);
        if (!decSID.equals(sid)) {
            logger.error("403");
            response.setStatus(403);
            return;
        }

        try {
            request.setCharacterEncoding("utf-8");
            response.setContentType("application/x-msdownload");

            String fileName = request.getHeader("fileName");

            System.out.println("fileName:" + fileName);
            MyFile myFile = new MyFile();
            IMyFileDao dao = new MyFileImpl();
            myFile = dao.getFile(fileName);
            File file = new File(myFile.getFileFolderPath() + "/" + myFile.getFileID());
            response.setContentLength((int) file.length());
            response.setCharacterEncoding("UTF-8");
            response.addHeader("encDigEnve", myFile.getEncDigEnve());
            response.addHeader("fileName", URLEncoder.encode(myFile.getFileName(), "UTF-8"));
            System.out.println(myFile.getFileName());
            response.setHeader("Accept-Ranges", "bytes");

            int readLength = 0;

            in = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
            out = new BufferedOutputStream(response.getOutputStream());

            byte[] buffer = new byte[BUFFER_SIZE];
            while ((readLength = in.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                out.write(bytes);
            }

            out.flush();

            response.addHeader("token", "hello 1");

        } catch (Exception e) {
            response.setStatus(410);
            logger.error("410 无此文件"+e.getMessage());
            e.printStackTrace();
            return;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage().toString());
                }
            }
        }
    }
}
