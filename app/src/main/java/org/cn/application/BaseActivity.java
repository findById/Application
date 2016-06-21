package org.cn.application;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.cn.core.permission.PermissionDispatcher;

/**
 * Created by chenning on 16-2-26.
 */
public class BaseActivity extends AppCompatActivity {
    public static final String TAG = BaseActivity.class.getSimpleName();

    public static final int REQUEST_PER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Received response for '"+permissions+"' permission request.");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionDispatcher.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
