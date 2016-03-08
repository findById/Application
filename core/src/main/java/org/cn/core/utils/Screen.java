package org.cn.core.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by chenning on 16-3-8.
 */
public class Screen {

    static Screen SCREEN = new Screen();

    public int widthPixels; // 屏幕宽
    public int heightPixels; // 屏幕高
    public int barHeight; // 状态栏高度
    public float density;
    public float scaledDensity;
    public int densityDpi;
    public float xdpi;
    public float ydpi;

    private Screen() {
    }

    public static void initScreen(Context ctx) {
        DisplayMetrics display = ctx.getResources().getDisplayMetrics();
        SCREEN.widthPixels = display.widthPixels;
        SCREEN.heightPixels = display.heightPixels;
        SCREEN.density = display.density;
        SCREEN.scaledDensity = display.scaledDensity;
        SCREEN.densityDpi = display.densityDpi;
        SCREEN.xdpi = display.xdpi;
        SCREEN.ydpi = display.ydpi;

        SCREEN.barHeight = getInternalDimensionSizeByKey(ctx, "status_bar_height");
    }

    public static int getInternalDimensionSizeByKey(Context ctx, String key) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static Screen getInstance() {
        return SCREEN;
    }
}
