package com.ling0322.lia;

import android.support.v4.app.*;
import android.util.Log;

import com.viewpagerindicator.*;

public class LiaFragmentAdapter extends FragmentPagerAdapter implements TitleProvider {
	public LiaFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	public String getTitle(int position) {
		switch(position) {
		case 0:
			return "词典";
		case 1:
			return "单词记忆";
		}
		return null;
	}

	@Override
	public Fragment getItem(int item) {
		Log.d("lia", "getItem called.");
		switch(item) {
		case 0:
			return new DictionaryActivity();
		case 1:
			return new ReciteActivity();
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}
}
