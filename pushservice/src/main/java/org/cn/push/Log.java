package org.cn.push;

public class Log {

    public static void d(String tag, String message) {
        android.util.Log.d(tag, message);
    }

    public static void e(String tag, String message) {
        android.util.Log.e(tag, message);
    }

    public static void e(String tag, String message, Throwable tx) {
        android.util.Log.e(tag, message, tx);
    }

}
