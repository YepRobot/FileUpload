package com.yan.utils;


import org.junit.Test;

import java.io.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;


public class TEST {
    @Test
    public  void TestRsa() {
        try {
            //===============生成公钥和私钥，公钥传给客户端，私钥服务端保留==================
            //生成RSA公钥和私钥，并Base64编码
            KeyPair keyPair = RSAUtil.getKeyPair();
            String publicKeyStr = RSAUtil.getPublicKey(keyPair);
            String privateKeyStr = RSAUtil.getPrivateKey(keyPair);
            System.out.println("RSA公钥Base64编码:" + publicKeyStr);
            System.out.println("RSA私钥Base64编码:" + privateKeyStr);
//            FileWriter writer;
//            writer = new FileWriter("C:/learn/pub.rsa");
//            writer.write("");//清空原文件内容
//            writer.write(publicKeyStr);
//            writer.flush();
//            writer.close();
//
//            FileWriter writer1;
//            writer1 = new FileWriter("C:/learn/pri.rsa");
//            writer1.write("");//清空原文件内容
//            writer1.write(privateKeyStr);
//            writer1.flush();
//            writer1.close();

            //=================客户端=================
            //hello, i am infi, good night!加密
            String message = "hello, i am infi, good night!";
            //将Base64编码后的公钥转换成PublicKey对象
            File file = new File("C:/learn/pub.rsa");//定义一个file对象，用来初始化FileReader
            FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
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
            publicKeyStr = str;
            PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr);
            //用公钥加密
            byte[] publicEncrypt = RSAUtil.blockEncrypt(message, publicKey);
            //加密后的内容Base64编码
            String byte2Base64 = RSAUtil.byte2Base64(publicEncrypt);
            System.out.println("公钥加密并Base64编码的结果：" + byte2Base64);


            //##############    网络上传输的内容有Base64编码后的公钥 和 Base64编码后的公钥加密的内容     #################



            //===================服务端================
            File filepri = new File("C:/learn/pri.rsa");//定义一个file对象，用来初始化FileReader
            FileReader readerpri = new FileReader(filepri);//定义一个fileReader对象，用来初始化BufferedReader
            BufferedReader bReaderpri = new BufferedReader(readerpri);//new一个BufferedReader对象，将文件内容读取到缓存
            StringBuilder sbpri = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
            String spri = "";
            while ((spri =bReaderpri.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                sbpri.append(spri);//将读取的字符串添加换行符后累加存放在缓存中
                System.out.println(spri);
            }
            bReaderpri.close();
            String strpri = sbpri.toString();
            System.out.println(strpri );
            privateKeyStr = strpri;

            //将Base64编码后的私钥转换成PrivateKey对象
            PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr);
            //加密后的内容Base64解码
            byte[] base642Byte = RSAUtil.base642Byte(byte2Base64);
            //用私钥解密
            String privateDecrypt = RSAUtil.blockDecryptByPrivateKey(base642Byte, privateKey);
            //解密后的明文
            System.out.println("解密后的明文: " + new String(privateDecrypt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void Testsid(){
        String message = "hello, i am infi, good night!";
        String byte2Base64=null;

        File filepri = new File("pri.rsa");//定义一个file对象，用来初始化FileReader
        FileReader readerpri = null;//定义一个fileReader对象，用来初始化BufferedReader
        try {
            readerpri = new FileReader(filepri);
            BufferedReader bReaderpri = new BufferedReader(readerpri);//new一个BufferedReader对象，将文件内容读取到缓存
            StringBuilder sbpri = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
            String spri = "";
            while ((spri =bReaderpri.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                sbpri.append(spri);//将读取的字符串添加换行符后累加存放在缓存中
                System.out.println(spri);
            }
            bReaderpri.close();
            String strpri = sbpri.toString();
            System.out.println(strpri );
            String privateKeyStr = strpri;
            PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr);
            byte[] privateEncrypt = RSAUtil.blockEncryptbyPrivate(message,privateKey);
            //加密后的内容Base64编码
            byte2Base64 = RSAUtil.byte2Base64(privateEncrypt);
            System.out.println("公钥加密并Base64编码的结果：" + byte2Base64);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        File file = new File("pub.rsa");//定义一个file对象，用来初始化FileReader
        FileReader reader = null;//定义一个fileReader对象，用来初始化BufferedReader
        try {
            reader = new FileReader(file);
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
            String publicKeyStr = str;
            PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr);
            byte[] base642Byte = RSAUtil.base642Byte(byte2Base64);
            //用私钥解密
            String publicDecrypt = RSAUtil.blockDecryptByPublicKey(base642Byte,publicKey);
            //解密后的明文
            System.out.println("解密后的明文: " + new String(publicDecrypt));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Test
    public void FileTest() throws IOException {
        File file = new File("pub.rsa");
        file.createNewFile();
    }

}