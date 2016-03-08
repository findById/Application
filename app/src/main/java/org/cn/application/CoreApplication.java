package org.cn.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by chenning on 16-2-26.
 */
public class CoreApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        System.gc();
    }

}
