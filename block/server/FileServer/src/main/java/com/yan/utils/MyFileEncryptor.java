package com.yan.utils;




import org.junit.Test;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class MyFileEncryptor {
    public static SecretKey getKeys(String uid) {
        KeyGenerator keyGenerator = null;
        OutputStream os;
        String keyString;
        SecretKey secretKey = null;
        final Base64.Decoder decoder = Base64.getDecoder();
        final Base64.Encoder encoder = Base64.getEncoder();
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, new SecureRandom(uid.getBytes()));
            secretKey = keyGenerator.generateKey();

            keyString = encoder.encodeToString(secretKey.getEncoded());
            System.out.println(keyString);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return secretKey;
    }


    public static Cipher initAESCipher(SecretKey secretKey, int cipherMode) {
        //创建Key gen
        KeyGenerator keyGenerator = null;
        Cipher cipher = null;
        OutputStream os;
        String keyString;
        final Base64.Decoder decoder = Base64.getDecoder();
        final Base64.Encoder encoder = Base64.getEncoder();
        try {
            byte[] codeFormat = secretKey.getEncoded();
            //将密钥转换为字符串
            keyString = encoder.encodeToString(secretKey.getEncoded());
            System.out.println(keyString);

            SecretKeySpec key = new SecretKeySpec(codeFormat, "AES");
            cipher = Cipher.getInstance("AES");
            //初始化
            cipher.init(cipherMode, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return cipher;
    }

    /**
     * 对文件进行AES加密
     *
     * @param sourceFile
     * @param saveFilePath
     * @param cipher
     * @return
     */
    public static File encryptFile(File sourceFile,String saveFilePath,  Cipher cipher) {
        //新建临时加密文件
        File encrypfile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {

            System.out.println(cipher);
            inputStream = new FileInputStream(sourceFile);
            encrypfile = new File(saveFilePath);

            outputStream = new FileOutputStream(encrypfile);
            //以加密流写入文件
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = cipherInputStream.read(cache)) != -1) {
                outputStream.write(cache, 0, nRead);
                outputStream.flush();
            }
            cipherInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return encrypfile;
    }

    /**
     * AES方式解密文件
     *
     * @param sourceFile
     * @return
     */
    public static File decryptFile(File sourceFile, String saveFilePath, Cipher cipher) {
        File decryptFile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            decryptFile = new File(saveFilePath);
            outputStream = new FileOutputStream(decryptFile);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, r);
            }
            cipherOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return decryptFile;
    }

    @Test
    public  void encryTest() throws IOException, NoSuchAlgorithmException {
        String path = "C:/learn/团队二等奖-闫鹏亮.jpg";
        File sourceFile = new File(path);
        System.out.println(sourceFile.getName());
        MyFileEncryptor myFileEncryptor = new MyFileEncryptor();
        String type;
        type = Files.probeContentType(Paths.get(path));
        System.out.println(type);
        File encrypfile;
        SecretKey secretKey = getKeys("yan");
        String keyString = RSAUtil.getAESKeyStr(secretKey);
        secretKey = RSAUtil.string2SecretKey(keyString);
        System.out.println(keyString);
        String saveFilePath="C:/learn/团队二-闫鹏亮.jpg";
        Cipher cipher = initAESCipher(secretKey, Cipher.ENCRYPT_MODE);
        encrypfile = myFileEncryptor.encryptFile(sourceFile, saveFilePath, cipher);
    }


    @Test
    public void deceyTest() throws IOException, NoSuchAlgorithmException {
        final Base64.Decoder decoder = Base64.getDecoder();
        final Base64.Encoder encoder = Base64.getEncoder();
        String path = "C:/learn/团队二-闫鹏亮.jpg";
        File sourceFile = new File(path);
        MyFileEncryptor myFileEncryptor = new MyFileEncryptor();
        String type;
        type = Files.probeContentType(Paths.get(path));
        System.out.println(type);
        File decrypfile;
        SecretKey secretKey = myFileEncryptor.getKeys("yan");
        String keyString = RSAUtil.getAESKeyStr(secretKey);
        secretKey = RSAUtil.string2SecretKey(keyString);
        System.out.println(keyString);
        String saveFilePath="C:/learn/团队二等奖1-闫鹏亮.jpg";
        Cipher cipher = myFileEncryptor.initAESCipher(secretKey, Cipher.DECRYPT_MODE);
        decrypfile = myFileEncryptor.decryptFile(sourceFile, saveFilePath, cipher);
    }

}