package org.cn.application.ui.suspension;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.cn.application.R;

/**
 * Created by chenning on 16-5-31.
 */
public class FloatManager {
    private static WindowManager mWindowManager;
    private static ActivityManager mActivityManager;

    private static WindowManager.LayoutParams mLayoutParams;

    private static FloatLayout view;

    public static void createFloat(Context ctx) {
        Toast.makeText(ctx, "Create float window", Toast.LENGTH_SHORT).show();
        WindowManager windowManager = getWindowManager(ctx);
        if (view == null) {
            view = new FloatLayout(ctx);
            view.setWindowManager(windowManager);
            View v = View.inflate(ctx, R.layout.model_float_window, null);
            view.addView(v);
            if (mLayoutParams == null) {
                mLayoutParams = getLayoutParams();
                view.setLayoutParams(mLayoutParams);
                mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
                mLayoutParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

                mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角，便于调整坐标
                //以屏幕左上角为原点，设置x、y初始值
                mLayoutParams.x = 0;
                mLayoutParams.y = 0;
                //设置悬浮窗口长宽数据
                mLayoutParams.width = ctx.getResources().getDisplayMetrics().widthPixels / 2;
                mLayoutParams.height = ctx.getResources().getDisplayMetrics().heightPixels / 4;
            }
            windowManager.addView(view, mLayoutParams); // 加入窗口
        }
    }

    public static void removeFloat(Context ctx) {
        if (view != null) {
            getWindowManager(ctx).removeView(view);
            view = null;
            mLayoutParams = null;
        }
    }

    public static WindowManager getWindowManager(Context ctx) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    private static ActivityManager getActivityManager(Context ctx) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }

    public static WindowManager.LayoutParams getLayoutParams() {
        if (mLayoutParams == null) {
            mLayoutParams = new WindowManager.LayoutParams();
        }
        return mLayoutParams;
    }
}
