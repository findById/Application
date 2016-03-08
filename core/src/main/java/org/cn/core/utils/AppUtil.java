package org.cn.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * Created by work on 16-3-8.
 */
public class AppUtil {
    private static final String TAG = "AppUtil";

    /* 取得系统的versionCode */
    public static int getVersionCode(Context ctx) {
        int versionCode = -1;
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getApplicationContext().getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if (pInfo != null) {
                versionCode = pInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e("versionCode not found");
        }
        return versionCode;
    }

    /* 取得系统的versionName */
    public static String getVersionName(Context ctx) {
        String versionName = "";
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getApplicationContext().getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if (pInfo != null) {
                versionName = pInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e("versionCode not found");
        }
        if (TextUtils.isEmpty(versionName)) {
            versionName = "";
        }
        return versionName;
    }

    public static boolean isNetworkAvailable(Context ctx) {
        Context context = ctx.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        // 获取NetworkInfo对象
        NetworkInfo[] networkInfoList = cm.getAllNetworkInfo();

        if (networkInfoList != null && networkInfoList.length > 0) {
            for (NetworkInfo networkInfo : networkInfoList) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    Logger.d(TAG, "===状态===" + networkInfo.getState());
                    Logger.d(TAG, "===类型===" + networkInfo.getTypeName());
                    return true;
                }
            }
        }
        return false;
    }

    public static String getIpAddress(Context ctx) {
        return "";
    }
}
