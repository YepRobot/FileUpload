package com.yan.springboot.controller;


import com.alibaba.fastjson.JSONObject;
import com.yan.utils.PermissionCheck;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Controller
public class UploadController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @PostMapping("/uploadFile")
    public String uploadImage(@RequestParam(value = "file") MultipartFile upload, Model model) {
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        //服务端要调用的外部接口
        String url ="http://127.0.0.1:8091/saveFile";
        //httpclients构造post请求
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            //HttpMultipartMode.RFC6532参数的设定是为避免文件名为中文时乱码
            MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);
            String originFileName = null;
            originFileName = upload.getOriginalFilename();
            // 设置上传文件流(需要是输出流)，设置contentType的类型
            builder.addBinaryBody("upload", upload.getBytes(), ContentType.MULTIPART_FORM_DATA, originFileName);
            //post请求中的其他参数
//            builder.addTextBody("params",params);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            String sid=PermissionCheck.getSID();
            String signature = PermissionCheck.SID2Signature(sid);
            httpPost.addHeader("X-SID", sid);
            httpPost.addHeader("X-Signature",signature);
            // 执行提交
            HttpResponse response = httpClient.execute(httpPost);
            //接收调用外部接口返回的内容
            HttpEntity responseEntity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200){
                //响应内容都存在content中
                InputStream content = responseEntity.getContent();
                in = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
               // JSONObject jsonObject = JSONObject.parseObject(result.toString());
                model.addAttribute("status","操作成功");
                model.addAttribute("message",response.getHeaders("uid")[0]);
                return "/status";
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }finally {//处理结束后关闭httpclient的链接
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
        model.addAttribute("status","操作失败");
        model.addAttribute("message","");
        return "/status";
    }
    @RequestMapping("/upload.html")
    public String index(){
        return "upload";
    }

    @RequestMapping("/list.html")
    public String list(){
        return "list";
    }
}