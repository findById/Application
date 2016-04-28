package org.cn.application.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

    private ProcessesLayout mProcessesLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
        }, 5000);
    }

}
