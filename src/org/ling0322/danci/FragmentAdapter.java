package org.ling0322.danci;

import android.app.Activity;
import android.support.v4.app.*;
import android.util.Log;



public class FragmentAdapter extends FragmentPagerAdapter {
	private BaseFragment[] fragment;
	private Activity mActivity;
    public FragmentAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        fragment = new BaseFragment[3];
        mActivity = activity;
    }
    
    
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
        case 0:
            return "词典";
        case 1:
            return "单词";
        case 2:
            return "生词本";
        }
        return null;
    }

    @Override
    public BaseFragment getItem(int item) {
        Log.d("lia", "getItem called -- " + item);
        switch(item) {
        case 0:
            if (fragment[0] == null)
        	fragment[0] = (BaseFragment) DictionaryFragment.instantiate(mActivity, "org.ling0322.danci.DictionaryFragment");
            return fragment[0];
        case 1:
            if (fragment[1] == null)
        	fragment[1] = (BaseFragment) ReciteFragment.instantiate(mActivity, "org.ling0322.danci.ReciteFragment");
            return fragment[1];
        case 2:
            if (fragment[2] == null)
        	fragment[2] = (BaseFragment) ReviewListFragment.instantiate(mActivity, "org.ling0322.danci.ReviewListFragment");
            return fragment[2];
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
