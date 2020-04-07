package com.yan.springboot.controller;

import com.yan.utils.MyFileEncryptor;
import com.yan.utils.PermissionCheck;
import com.yan.utils.RSAUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.PrivateKey;

@Controller
public class DownloadController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @RequestMapping("/downloadFile")
    public void clientDown(HttpServletRequest request, HttpServletResponse response, Model mod) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        OutputStream out = null;
        InputStream in = null;

        String urlPath = "http://localhost:8091:/realdwonloadFile";
//        String remoteFilePath  = "D:\\usr\\local\\ciecc\\testdown\\";

        String fileName = request.getParameter("uid");
        try {
            HttpPost httpPost = new HttpPost(urlPath);

            httpPost.addHeader("fileName", fileName);
            String sid= PermissionCheck.getSID();
            String signature = PermissionCheck.SID2Signature(sid);
            httpPost.addHeader("X-SID", sid);
            httpPost.addHeader("X-Signature",signature);


            httpPost.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            //这里访问server 端（也就是urlPath路径），server下载文件并将文件流回传
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if(httpResponse.getStatusLine().getStatusCode()==403){
                logger.trace("403");
                response.sendRedirect("/403.html");
            }
            if(httpResponse.getStatusLine().getStatusCode()==410){
                logger.trace("410");
                response.sendRedirect("/410.html");
            }

            HttpEntity entity = httpResponse.getEntity();
            in = entity.getContent();

            long length = entity.getContentLength();
            if (length <= 0) {
                logger.error("下载文件不存在！");
                System.out.println("下载文件不存在！");
                return;
            }

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/x-msdownload");
            response.setHeader("Accept-Ranges", "bytes");

            System.out.println(httpResponse.getHeaders("fileName")[0]);
            String encDigEnve = (httpResponse.getHeaders("encDigEnve")[0]).toString().split(": ")[1];
            String saveFileName = (httpResponse.getHeaders("fileName")[0]).toString().split(":")[1];
            response.setHeader("Content-Disposition", "attachment;filename=" + saveFileName);

            //下载文件并解密
            File saveFile = new File("download/saveFile");
            FileOutputStream fos = new FileOutputStream(saveFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int len =0;
            while((len = in.read())!=-1){
                bos.write(len);
                bos.flush();
            }

            bos.close();
            //解密文件
            File prikeyfile = new File("pri.rsa");//定义一个file对象，用来初始化FileReader
            FileReader reader = new FileReader(prikeyfile);//定义一个fileReader对象，用来初始化BufferedReader
            BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
            StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
            String s = "";
            while ((s =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                sb.append(s);//将读取的字符串添加换行符后累加存放在缓存中
                System.out.println(s);
            }
            bReader.close();
            String str = sb.toString();
            System.out.println(str );
            String privateKeyStr = str;
            System.out.println("\n"+encDigEnve);
//            encDigEnve="R3YE7m9gQE4BMVS90UmWxksZbeWmGD9Q23uVVX3+Da9Fx7/bU+MVjlcNgESvtGuqMrGzzPwDcnKT\n" +
//                    "nMjY+61JEyKJd0xqGX1krMiyxw+20GEZ1Og+Sg9W9WeX/SI7ytkmSAia/uKrQ266NMdFFEC7r2oU\n" +
//                    "N2Tel2P6ctYl5h90rrI=";

            String[] encDigEnveafter =encDigEnve.split("  ");
            encDigEnve=encDigEnveafter[0]+"\n"+encDigEnveafter[1]+"\n"+encDigEnveafter[2];

            PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr);
            //加密后的内容Base64解码
            byte[] base642Byte = RSAUtil.base642Byte(encDigEnve);
            //用私钥解密
            String privateDecrypt = RSAUtil.blockDecryptByPrivateKey(base642Byte, privateKey);
            //解密后的明文
            System.out.println("解密后的明文: " + privateDecrypt);
            //获得SecretKey
            String AESkeyStr = privateDecrypt;
            SecretKey secretKey = RSAUtil.string2SecretKey(AESkeyStr);
            String saveFilePath="download/tempFile";
            Cipher cipher = MyFileEncryptor.initAESCipher(secretKey, Cipher.DECRYPT_MODE);
            File decrypfile = MyFileEncryptor.decryptFile(saveFile, saveFilePath, cipher);


            in = new FileInputStream(decrypfile);
             out = new BufferedOutputStream(response.getOutputStream());
            byte[] buffer = new byte[1024];
            int readLength = 0;
            while ((readLength = in.read(buffer)) > 0) {
                byte[] bytes2 = new byte[readLength];
                System.arraycopy(buffer, 0, bytes2, 0, readLength);
                out.write(bytes2);
            }

            out.flush();


        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }

        }

    @RequestMapping("/410.html")
    public String page401(){
        return "/410.html";
    }
    @RequestMapping("/403.html")
    public String page403(){
        return "/403.html";
    }
}



