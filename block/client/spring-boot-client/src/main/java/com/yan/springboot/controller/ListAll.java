package com.yan.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.yan.domain.MyFile;
import com.yan.utils.PermissionCheck;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ListAll {
    Logger logger = LoggerFactory.getLogger(getClass());
    @RequestMapping(value = {"/index.html", "/", "/index"})
    public String listAll(Model model) {
        OutputStream out = null;
        InputStream in = null;
        try {
            List<MyFile> fileList = new ArrayList<MyFile>();
            String url = "http://127.0.0.1:8091/fileListAll";
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            String sid = PermissionCheck.getSID();
            String signature = PermissionCheck.SID2Signature(sid);
            httpGet.addHeader("X-SID", sid);
            httpGet.addHeader("X-Signature", signature);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            in = entity.getContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i;
            while ((i = in.read()) != -1) {
                baos.write(i);
            }
            String str = baos.toString();
            str="["+str+"]";
            System.out.println(str);
            List<MyFile> myFileList = JSONObject.parseArray(str, MyFile.class);
           System.out.println("*****************************************************");
            System.out.println(myFileList);
            model.addAttribute("fileList",myFileList);


        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    return"index";
    }

}