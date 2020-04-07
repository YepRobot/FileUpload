package com.yan.utils;



import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

public class PermissionCheck {

    public static String getSID(){
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String target="";
        Random r = new Random();
        for(int i =0;i<10;i++) {
            int a=r.nextInt(str.length());
           target=target+str.substring(a,a+1);
        }
        System.out.println(target);
    return target;
    }

    public static String SID2Signature(String sid){
        String message = sid;
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
        return byte2Base64;
    }
    public static String Signature2SID(String signature){
        String byte2Base64=signature;
        String publicDecrypt=null;
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
            publicDecrypt = RSAUtil.blockDecryptByPublicKey(base642Byte,publicKey);
            //解密后的明文
            System.out.println("解密后的明文: " + new String(publicDecrypt));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return publicDecrypt;
    }

    public static void main(String[] args) {
        String sid= PermissionCheck.getSID();
        String signature = PermissionCheck.SID2Signature(sid);
       System.out.println("X-SID:"+sid);
       System.out.println("X-Signature:"+signature);
       String serverSig = PermissionCheck.Signature2SID(signature);
       System.out.println("ServerSignature:"+serverSig);
    }
}
