package org.cn.application;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * Created by chenning on 16-2-29.
 */
public class SmartActivity extends BaseActivity {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
    }

    public Fragment onChangeFragment(String key) {
        if (key == null) {
            return null;
        }
        Fragment tmp = mFragmentManager.findFragmentByTag(key);
        if (tmp == null) {
            return null;
        }

        List<Fragment> list = mFragmentManager.getFragments();
        if (list != null && list.size() > 0) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                mFragmentTransaction.detach(list.get(i));
            }
            mFragmentTransaction.commit();
        }
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (currentFragment != null) {
            mFragmentTransaction.detach(currentFragment);
        }
        mFragmentTransaction.attach(tmp);
        mFragmentTransaction.commit();

        currentFragment = tmp;
        return currentFragment;
    }

    public FragmentTransaction add(Fragment fragment, String tag, Bundle data) {
        List<Fragment> list = mFragmentManager.getFragments();
        if (list != null && list.size() > 0) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                mFragmentTransaction.detach(list.get(i));
            }
            mFragmentTransaction.commit();
        }
        Fragment tmp = mFragmentManager.findFragmentByTag(tag);
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (tmp != null) {
            mFragmentTransaction.remove(tmp);
        }
        if (data != null) {
            fragment.setArguments(data);
        }
        mFragmentTransaction.add(R.id.contentPanel, fragment, tag);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
        // mFragmentManager.executePendingTransactions();
        return mFragmentTransaction;
    }

}
