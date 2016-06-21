package org.cn.application.ui.tab;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.alibaba.fastjson.JSON;

import org.cn.application.BaseActivity;
import org.cn.application.R;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenning on 16-4-28.
 */
public class TabActivity extends BaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        initView();
    }

    private void initView() {
        mTabLayout = (TabLayout) this.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) this.findViewById(R.id.view_pager);

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        List<TabMenu> list = new ArrayList<>();
        try {
            InputStream is = getResources().getAssets().open("tabs.json");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 8];
            int len;
            while ((len = is.read(buffer)) >= 0) {
                baos.write(buffer, 0, len);
            }
            list.addAll(JSON.parseArray(baos.toString(), TabMenu.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Adapter adapter = new Adapter(this.getSupportFragmentManager());
        adapter.updateData(list);
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

    }

    class Adapter extends FragmentPagerAdapter {

        private List<TabMenu> data;

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void updateData(List<TabMenu> data) {
            if (data == null || data.isEmpty()) {
                return;
            }
            if (this.data == null) {
                this.data = new ArrayList<>();
            }
            this.data.clear();
            for (TabMenu m : data) {
                if (m.isAttached()) {
                    this.data.add(m);
                }
            }
            // this.data.addAll(data);
            if (this.data.size() <= 3) {
                mTabLayout.setTabMode(TabLayout.MODE_FIXED);
            }
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString(TabFragment.ACTION, data.get(position).getAction());

            Fragment fragment = new TabFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return data == null || data.isEmpty() ? 0 : data.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(data.get(position).getTitle());
        }
    }

}
