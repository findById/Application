package org.cn.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import org.cn.core.utils.LocateManager;
import org.cn.push.core.PushService;

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
//        startService(new Intent(getApplicationContext(), PushService.class));
//
//        LocateManager lm = new LocateManager();
//        lm.getLocation(getApplicationContext());
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
