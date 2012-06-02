package org.ling0322.danci;

import java.util.ArrayList;

import android.support.v4.app.*;
import android.util.Log;
import android.view.View;

import com.viewpagerindicator.*;

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        fragment = new CustomFragment[3];
    }
    
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("FragmentAdapter", "notifyDataSetChanged");
        fragment = new CustomFragment[3];
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

    private CustomFragment[] fragment;
    
    @Override
    public CustomFragment getItem(int item) {
        Log.d("lia", "getItem called -- " + item);
        switch(item) {
        case 0:
            if (fragment[0] == null)
        	fragment[0] = new DictionaryFragment();
            return fragment[0];
        case 1:
            if (fragment[1] == null)
        	fragment[1] = new ReciteFragment();
            return fragment[1];
        case 2:
            if (fragment[2] == null)
        	fragment[2] = new ReviewListFragment();
            return fragment[2];
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
