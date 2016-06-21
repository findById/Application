package org.cn.application.ui.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cn.application.BaseActivity;
import org.cn.application.R;
import org.cn.core.utils.Screen;
import org.cn.core.utils.SystemUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenning on 16-4-18.
 */
public class StatusInfoActivity extends BaseActivity {
    private static final String TAG = StatusInfoActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private InfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_information);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
        initData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        mAdapter = new InfoAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        showSystemInfo(this);
    }

    List<InfoBean> data = new ArrayList<>(30);

    public String[] showSystemInfo(Context ctx) {


        String[] result = new String[16];


        Log.d(TAG, "手机型号: " + Build.MODEL);
        Log.d(TAG, "系统CODE: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "系统版本: " + Build.VERSION.RELEASE);
        Log.d(TAG, "品牌: " + Build.BRAND);
        Log.d(TAG, "用户: " + Build.USER);
        Log.d(TAG, "系统: " + Build.DISPLAY);
        Log.d(TAG, "制造商: " + Build.MANUFACTURER);

        Screen.initScreen(this);

        StringBuffer sb = new StringBuffer();
        sb.append(Screen.getInstance().heightPixels);
        sb.append(" * ");
        sb.append(Screen.getInstance().widthPixels);
        sb.append("(Dpi:");
        sb.append(Screen.getInstance().densityDpi);
        sb.append(", ");
        int screen = Screen.getInstance().widthPixels * Screen.getInstance().heightPixels;
        if (screen >= (1920 * 1080)) {
            sb.append("xxhdpi");
        } else if (screen >= 1280 * 720) {
            sb.append("xhdpi");
        } else if (screen >= 960 * 540) {
            sb.append("hdpi");
        } else {
            sb.append("hdpi");
        }
        sb.append(")");
        Log.d(TAG, "屏幕分辨率: " + sb.toString());

        String[] net = SystemUtil.getNetworkInfo(this);
        Log.d(TAG, "网络情况: " + net[0]);
        Log.d(TAG, "网络类型: " + net[1]);

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //得到可用内存
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        Log.d(TAG, "内存大小: " + String.format("%.2fGB", ((memoryInfo.totalMem - memoryInfo.availMem) / 1024. / 1024 / 1024)) + "/"
                + String.format("%.2fGB", (memoryInfo.totalMem / 1024. / 1024 / 1024)));

        String[] hardware = SystemUtil.getHardwareInfo();
        Log.d(TAG, "内核版本: " + hardware[0]);
        Log.d(TAG, "处理器类型: " + hardware[1]);
        Log.d(TAG, "CPU主频: " + hardware[2] + " * " + hardware[3]);
        Log.d(TAG, "内存大小: " + hardware[5] + "/" + hardware[4]);

        data.add(new InfoBean("网络状态", 1));
        data.add(new InfoBean(0, "网络情况", net[0]));
        if ("connected".equals(net[0])) {
            data.add(new InfoBean(0, "网络类型", net[1]));
            if (!"WIFI".contains(net[1])) {
//                data.add(new InfoBean(0, "网络类型", net[2]));
//                data.add(new InfoBean(0, "网络类型", net[3]));
            }
        }
        data.add(new InfoBean("系统信息", 1));
        data.add(new InfoBean(0, "品牌", Build.BRAND));
        data.add(new InfoBean(0, "用户", Build.USER));
        data.add(new InfoBean(0, "系统", Build.DISPLAY));
        data.add(new InfoBean(0, "内核版本", hardware[0]));
        data.add(new InfoBean(0, "系统版本", Build.VERSION.RELEASE));
        data.add(new InfoBean(0, "是否ROOT", (Boolean.valueOf((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) ? "否" : "是")));

        data.add(new InfoBean("硬件信息", 1));
        data.add(new InfoBean(0, "制造商", Build.MANUFACTURER));
        data.add(new InfoBean(0, "手机型号", Build.MODEL));
        data.add(new InfoBean(0, "处理器类型", hardware[1]));
        data.add(new InfoBean(0, "CPU主频", hardware[2] + " * " + hardware[3]));
        data.add(new InfoBean(0, "内存大小", String.format("%.2fGB", ((memoryInfo.totalMem - memoryInfo.availMem) / 1024. / 1024 / 1024)) + "/"
                + String.format("%.2fGB", (memoryInfo.totalMem / 1024. / 1024 / 1024))));
        data.add(new InfoBean(0, "屏幕分辨率", sb.toString()));

        SystemUtil.getBatteryInfo(this, new SystemUtil.OnResultListener() {
            @Override
            public void onResult(String[] results) {
                Log.d(TAG, "电池电量" + results[0]);
                Log.d(TAG, "电池技术" + results[1]);

                data.add(new InfoBean(0, "电池电量", results[0]));
                data.add(new InfoBean(0, "电池技术", results[1]));
                mAdapter.update(data);
            }
        });

        Log.d(TAG, SystemUtil.getCpuInfo());

        return result;
    }

    public class InfoBean {
        public int position;
        public String key;
        public String value;
        public int type;

        public InfoBean(int position, String key, String value) {
            this.position = position;
            this.key = key;
            this.value = value;
            this.type = 0;
        }

        public InfoBean(String key, int type) {
            this.key = key;
            this.type = type;
        }
    }

    class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private List<InfoBean> mData;

        public InfoAdapter(Context ctx) {
            this.mInflater = LayoutInflater.from(ctx);
            mData = new ArrayList<>();
        }

        public void update(List<InfoBean> data) {
            if (mData != null) {
                mData.clear();
            }
            mData.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mInflater.inflate(R.layout.item_status_information, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.onBindData(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_title, tv_content;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_title = (TextView) itemView.findViewById(R.id.tv_title);
                tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            }

            public void onBindData(InfoBean item) {
                if (item.type == 1) {
                    tv_title.setVisibility(View.GONE);
                    tv_content.setText(item.key);
                } else {
                    tv_title.setVisibility(View.VISIBLE);
                    tv_title.setText(item.key);
                    tv_content.setText(item.value);
                }
            }
        }
    }

}
