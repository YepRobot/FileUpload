package com.yan.controller;


import com.yan.dao.IMyFileDao;
import com.yan.dao.impl.MyFileImpl;
import com.yan.domain.MyFile;
import com.yan.utils.MyFileEncryptor;
import com.yan.utils.PermissionCheck;
import com.yan.utils.RSAUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.PublicKey;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.File;
import java.io.IOException;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class UploadServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(UploadServlet.class);
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }


    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
//req.setCharacterEncoding("text/html;charset=UTF-8");
//从request当中获取流信息
        String sid=req.getHeader("X-SID");
        System.out.println("原始X-SID:"+sid);
        String signature=req.getHeader("X-Signature");
        System.out.println("原始X-Signature:"+signature);
        String[] sigarr=signature.split("  ");
        String sig2sid=sigarr[0]+"\n"+sigarr[1]+"\n"+sigarr[2];
        String decSID= PermissionCheck.Signature2SID(sig2sid);
        System.out.println("解密后X-SID:"+decSID);

        if(!decSID.equals(sid)){
            logger.error("403权限错误");
            resp.setStatus(403);
            return;
        }
        InputStream fileSource = req.getInputStream();
        String tempFileName = "temp/tempFile";
//tempFile指向临时文件
        File tempFile = new File(tempFileName);
//outputStram文件输出流指向这个临时文件
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte b[] = new byte[1024];
        int n;
        while ((n = fileSource.read(b)) != -1) {
            outputStream.write(b, 0, n);
        }
//关闭输出流、输入流
        outputStream.close();
        fileSource.close();

//获取上传文件的名称
        RandomAccessFile randomFile = new RandomAccessFile(tempFile, "r");//RandomAccessFile是用来访问那些保存数据记录的文件的，第二个参数“r”表示只读。
        randomFile.readLine();//读取第一行数据
        String str = randomFile.readLine();//读取第二行数据，为什么读取了两次，看临时文件的保存的内容便知，第一行，和最后一行都是无用的内容数据，所以直接从第二行读取。

        String str1 = str.substring(0, str.lastIndexOf("\""));//根据最后一个引号来定位
        int endIndex = str.lastIndexOf("\"");
        int beginIndex = str1.lastIndexOf("\"") + 1;//beginIndex，endIndex，得到文件name 的初始位置。

        String filename = str.substring(beginIndex, endIndex);


        filename = new String(filename.getBytes("ISO-8859-1"), "utf-8");//因为从记事本中读取出来的是ISO-8859-1，所以需要转码，防止乱码

        System.out.println("filename:" + filename);

//重新定位文件指针到文件头
        randomFile.seek(0);// seek方法可以在文件中移动。
        long startPosition = 0;
        int i = 1;
//获取文件内容 开始位置
        while ((n = randomFile.readByte()) != -1 && i <= 5) {
            if (n == '\n') {
                startPosition = randomFile.getFilePointer();//.getFilePointer()用于定位
                i++;
            }
        }
        startPosition = randomFile.getFilePointer() - 1;
//获取文件内容 结束位置
        randomFile.seek(randomFile.length());
        long endPosition = randomFile.getFilePointer();
        int j = 1;
        while (endPosition >= 0 && j <= 2) {
            endPosition--;
            randomFile.seek(endPosition);
            if (randomFile.readByte() == '\n') {
                j++;
            }
        }
        endPosition = endPosition - 1;

//设置保存上传文件的路径

        MyFile file = new MyFile();
        file.setFileName(filename);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dtsave = sdf.format(date);
        file.setFileCreateTime(dtsave);
        SimpleDateFormat sd2 = new SimpleDateFormat("yyyyMMdd");
        String dpath = sd2.format(date);

        String realPath = "upload/"+dpath;//为文件保存的绝对路径
        File fileupload = new File(realPath);
        if (!fileupload.exists()) {
            fileupload.mkdir();
        }

        File saveFile = new File(fileupload, filename);
        //文件类型
        String fileType= Files.probeContentType(Paths.get(realPath+filename));
        file.setFileType(fileType);
        RandomAccessFile randomAccessFile = new RandomAccessFile(saveFile, "rw");
//从临时文件当中读取文件内容（根据起止位置获取）
        randomFile.seek(startPosition);
        while (startPosition < endPosition) {
            randomAccessFile.write(randomFile.readByte());
            startPosition = randomFile.getFilePointer();
        }
//关闭输入输出流、删除临时文件
        randomAccessFile.close();
        randomFile.close();

//        tempFile.delete();

        //加密保存
        String uid = UUID.randomUUID().toString();
        file.setFileID(uid);
        SecretKey secretKey = MyFileEncryptor.getKeys(uid);
        String keyString = RSAUtil.getAESKeyStr(secretKey);
        String saveFilePath=realPath+"/"+uid;
        Cipher cipher = MyFileEncryptor.initAESCipher(secretKey, Cipher.ENCRYPT_MODE);
        File encrypfile = MyFileEncryptor.encryptFile(saveFile, saveFilePath, cipher);
        File pubkeyFIle = new File("pub.rsa");//定义一个file对象，用来初始化FileReader
        FileReader reader = new FileReader(pubkeyFIle);//定义一个fileReader对象，用来初始化BufferedReader
        BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
        StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
        String s = "";
        while ((s =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
            sb.append(s);//将读取的字符串添加换行符后累加存放在缓存中
            System.out.println(s);
        }
        bReader.close();
        String pubStr = sb.toString();
        String publicKeyStr = pubStr;
        try {
            PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr);
            byte[] publicEncrypt = RSAUtil.blockEncrypt(keyString,publicKey);
            //加密后的内容Base64编码
            String byte2Base64 = RSAUtil.byte2Base64(publicEncrypt);
            System.out.println("公钥加密并Base64编码的结果：" + byte2Base64);
            //保存加密数字信封
            file.setEncDigEnve(byte2Base64);
            //删除源文件
            //saveFile.delete();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();

        }

        //加密结束

        resp.addHeader("uid",uid);
        file.setFileFolderPath(realPath);
        IMyFileDao dao = new MyFileImpl();
        try {
            dao.saveFile(file);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        System.out.println(file.toString());
    }
}