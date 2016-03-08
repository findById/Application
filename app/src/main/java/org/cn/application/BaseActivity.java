package org.cn.application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by chenning on 16-2-26.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
