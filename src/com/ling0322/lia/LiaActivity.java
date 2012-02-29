package com.ling0322.lia;

import com.viewpagerindicator.TabPageIndicator;

import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;

public class LiaActivity 
    extends FragmentActivity 
    implements OnClickListener, ViewPager.OnPageChangeListener { 
    /** Called when the activity is first created. */
	
	private ViewPager mPager = null;
	private TabPageIndicator mIndicator = null;
	
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tabs);
		
		Lia.mainInstance = this;
		
		LiaFragmentAdapter mAdapter = new LiaFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(1);
		
		mIndicator = (TabPageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(this);
		mIndicator.setCurrentItem(1);
		
		Log.d("lia", "main on_create");
    }

	public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button1:
            startActivity(new Intent(LiaActivity.this, DictionaryActivity.class));
            break;
        case R.id.button2:
            startActivity(new Intent(LiaActivity.this, ReciteActivity.class));
            break;
        }
	}
	
	//
	// menu
	//
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "选项").setIcon(R.drawable.setting_menu);
		menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "退出").setIcon(R.drawable.exit);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST:
			Intent it = new Intent(this, LiaPreferencesActivity.class);
			startActivity(it);
			break;
		case Menu.FIRST + 1:
			System.exit(0);
			break;
		}
		return false;
	}
	
	//
	//
	//
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//
		// Here intercept the BACK_KEY press event to
		// prevent unwanted exit of this app when users press BACK_KEY
		//
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mPager.setCurrentItem(1, true);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d("lia", "main on_resume");
		// closeIME();
	}

	private void closeIME() {
		EditText et = (EditText)findViewById(R.id.editText1);
		et.clearFocus();
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		Log.d("lia", "close input method");
	}
	public void onPageScrollStateChanged(int arg0) {
		//
		// close input method when switch fragments
		//
		closeIME();
	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	public void onPageSelected(int arg0) {
	}
}