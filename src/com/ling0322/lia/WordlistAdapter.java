package com.ling0322.lia;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;

public class WordlistAdapter extends BaseAdapter {

	ArrayList<String> list = null;
	Context context;
	public WordlistAdapter(ArrayList<String> list, Context context) {
		this.list = list;
		this.context = context;
	}
	
	public int getCount() {
		return list.size();
	}

	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup parent) {
		LayoutInflater li = LayoutInflater.from(context);
		View view = li.inflate(R.layout.wordlist_item, null);
		TextView tv = (TextView)view;
		tv.setText(list.get(arg0));
		return tv;
	}

}
