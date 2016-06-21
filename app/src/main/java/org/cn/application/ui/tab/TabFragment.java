package org.cn.application.ui.tab;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cn.application.BaseFragment;
import org.cn.application.R;
import org.cn.core.widget.ProcessesLayout;

/**
 * Created by chenning on 16-4-28.
 */
public class TabFragment extends BaseFragment {
    public static final String ACTION = "action";

    private ProcessesLayout mProcessesLayout;

    private String action;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            action = savedInstanceState.getString("action", "");
        }
        if (TextUtils.isEmpty(action) && getArguments() != null) {
            action = getArguments().getString(ACTION);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("action", action);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view) {
        mProcessesLayout = (ProcessesLayout) view.findViewById(R.id.processesLayout);
        mProcessesLayout.showProgress();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProcessesLayout.showContent();
            }
        }, 200);
    }

}
