package org.cn.core.utils;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by chenning on 16-3-8.
 */
public class AESUtil {

    private static final int KEY_SIZE = 128;
    private static final String KEY_ALGORITHM = "AES";

    public static String initKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        kg.init(KEY_SIZE);
        SecretKey secretKey = kg.generateKey();
        return Base64Util.encodeToString(secretKey.getEncoded());
    }

    public static String encrypt(String text, String key) {
        if (text == null || "".equals(text)) {
            return "";
        }

        String result = "";

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("utf-8");
            int len = b.length;
            if (len > keyBytes.length) {
                len = keyBytes.length;
            }
            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
            result = Base64Util.encodeToString(results);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String decrypt(String text, String key) {
        if (text == null || "".equals(text)) {
            return "";
        }

        String result = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("utf-8");
            int len = b.length;
            if (len > keyBytes.length) {
                len = keyBytes.length;
            }
            System.arraycopy(b, 0, keyBytes, 0, len);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] results = cipher.doFinal(Base64Util.decode(text.getBytes("UTF-8")));
            result = new String(results, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
