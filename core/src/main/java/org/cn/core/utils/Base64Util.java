package org.cn.core.utils;

import android.util.Base64;

import java.nio.charset.Charset;

/**
 * Created by chenning on 16-3-8.
 */
public class Base64Util {

    public static byte[] encode(byte[] bytes) {
        return Base64.encode(bytes, Base64.DEFAULT);
    }

    public static byte[] decode(byte[] bytes) {
        return Base64.decode(bytes, Base64.DEFAULT);
    }

    public static String encodeToString(byte[] bytes) {
        return new String(encode(bytes), Charset.forName("UTF-8"));
    }

    public static String decodeToString(byte[] bytes) {
        return new String(decode(bytes), Charset.forName("UTF-8"));
    }

}
