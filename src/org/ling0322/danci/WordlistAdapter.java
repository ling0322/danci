package org.ling0322.danci;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;

public class WordlistAdapter extends BaseAdapter {

	ArrayList<String> list = null;
	ArrayList<View> viewsList = null;
	Context context;
	public WordlistAdapter(ArrayList<String> list, Context context) {
		this.list = list;
		this.context = context;
		notifyDataSetChanged();
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

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		int length = list.size();
		viewsList = new ArrayList<View>(length);
		for (int i = 0; i < length; ++i) {
			viewsList.add(null);
		}
	}
	
	public View getView(int arg0, View arg1, ViewGroup parent) {
		if (viewsList.get(arg0) != null)
			return viewsList.get(arg0);
		
		LayoutInflater li = LayoutInflater.from(context);
		View view = li.inflate(R.layout.wordlist_item, null);
		TextView tv = (TextView)view;
		tv.setText(list.get(arg0));
		viewsList.set(arg0, tv);
		return tv;
	}

}
