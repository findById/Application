package org.cn.core.utils;

import android.util.Log;

import java.util.Locale;

public class Logger {

    private static final String TAG = "";
    private static final boolean DEBUG = true; // BuildConf.DEBUG

    public static void d(String format, Object... args) {
        if (DEBUG) {
            Log.d(TAG, buildMessage(format, args));
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            if (tag.contains("%")) {
                Log.d(TAG, buildMessage(tag, msg));
            } else {
                Log.d(tag, msg);
            }
        }
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            if (tag.contains("%")) {
                Log.e(TAG, buildMessage(tag, msg));
            } else {
                Log.e(tag, msg);
            }
        }
    }

    /**
     * logcat日志显示格式 [Thread_id - 线程id] 调用类名.方法（行号）
     */
    private static String buildMessage(String format, Object... args) {
        String msg = (args == null || args.length == 0) ? format : String.format(Locale.US, format, args);
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(Logger.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                caller = callingClass + "." + trace[i].getMethodName() + "(" + trace[i].getLineNumber() + ")";
                break;
            }
        }
        return String.format(Locale.US, "[Thread_id - %-3d] %-20s ：%s", Thread.currentThread().getId(), caller, msg);
    }

}
