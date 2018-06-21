package com.jld.obdserialport.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private static SecretKey generateKey(String seed) throws Exception {
        // 获取秘钥生成器
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        // 通过种子初始化
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        secureRandom.setSeed(seed.getBytes("UTF-8"));
        keyGenerator.init(128, secureRandom);
        // 生成秘钥并返回
        return keyGenerator.generateKey();
    }

    public static String encrypt(String content, String seed) throws Exception {
        SecretKey secretKey = generateKey(seed);
        // 秘钥
        byte[] enCodeFormat = secretKey.getEncoded();
        // 创建AES秘钥
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化加密器
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 加密
        byte[] bytes = cipher.doFinal(content.getBytes("UTF-8"));
        return parseByte2HexStr(bytes);
    }
    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
//    public static byte[] encrypt(String content, String password) throws Exception {
//        // 创建AES秘钥
//        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES/CBC/PKCS5PADDING");
//        // 创建密码器
//        Cipher cipher = Cipher.getInstance("AES");
//        // 初始化加密器
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//        // 加密
//        return cipher.doFinal(content.getBytes("UTF-8"));
//    }

    public static String decrypt(byte[] content, String seed) throws Exception {
        SecretKey secretKey = generateKey(seed);
        // 秘钥
        byte[] enCodeFormat = secretKey.getEncoded();
        // 创建AES秘钥
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化解密器
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(content);
        // 解密
        return new String(bytes,"UTF-8");
    }
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789abcdef".indexOf(c);
        return b;
    }
}
