package org.cn.application.ui.suspension;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.cn.push.Log;

/**
 * Created by chenning on 16-5-31.
 */
public class FloatedService extends Service {
    private static final String TAG = "float";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "service created.");
        FloatManager.createFloat(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service exec command >> " + intent.getAction());
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "service destroy.");
        FloatManager.removeFloat(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
