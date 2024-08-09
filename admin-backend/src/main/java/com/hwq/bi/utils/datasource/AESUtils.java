package com.hwq.bi.utils.datasource;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * @author HWQ
 * @date 2024/5/24 23:26
 * @description: AES加密工具类
 */
public class AESUtils {
    /**
     * 加密方法，采用AES_128位
     * @param plainText
     * @param secretKey
     * @return
     */
    public static String encrypt(String plainText, String secretKey) {
        if (secretKey == null) {
            throw new IllegalArgumentException("sSrc不能为空");
        }
        // 判断Key是否为16位
        if (secretKey.length() != 16) {
            throw new IllegalArgumentException("sKey长度需要为16位");
        }

        try {
            byte[] raw = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretkeySpec = new SecretKeySpec(raw, "AES");
            //"算法/模式/补码方式"
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretkeySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            //此处使用BASE64做转码功能，同时能起到2次加密的作用。
            return new Base64().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密方法 对AES_128位解密
     * @param cipherText
     * @return
     */
    public static String decrypt(String cipherText, String secretKey) {
        try {
            byte[] raw = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretkeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, secretkeySpec);
            //先用base64解密
            byte[] encrypted1 = new Base64().decode(cipherText);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, StandardCharsets.UTF_8);
            return originalString;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
