package com.wuzl.im.common.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wuzl.im.common.exception.ImException;

public class AesUtil {

    private static final Logger logger = LoggerFactory.getLogger("PROJECT_ERROR");

    public static byte[] encrypt(byte[] byteContent, byte[] key) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = null;// "算法/模式/补码方式"
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(byteContent);
            return encrypted;
        } catch (Exception e) {
            logger.error("加密失败", e);
            throw new ImException("加密失败");
        }
    }

    public static byte[] decrypt(byte[] content, byte[] key) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] original = cipher.doFinal(content);
            return original;
        } catch (Exception e) {
            logger.error("解密失败", e);
            throw new ImException("解密失败");
        }
    }
}
