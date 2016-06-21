package org.cn.application.ui.suspension;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.cn.application.BaseActivity;
import org.cn.application.R;

/**
 * Created by chenning on 16-5-31.
 */
public class FloatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_window);
        String[] permissions = new String[] {Manifest.permission.SYSTEM_ALERT_WINDOW};

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FloatedService.class);
                intent.setAction("asdfasdf");
                startService(intent);
            }
        });

        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FloatActivity.this, FloatedService.class);
                intent.setAction("stop");
                stopService(intent);
            }
        });
    }
}
