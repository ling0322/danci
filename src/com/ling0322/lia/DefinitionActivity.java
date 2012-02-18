package com.ling0322.lia;


import java.util.*;

import org.json.*;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class DefinitionActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String word = getIntent().getExtras().getString("word");

		
		View sv = DefinitionView.getDefinitionView(this, word);
		LinearLayout ll = new LinearLayout(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT,
			LinearLayout.LayoutParams.FILL_PARENT);
		lp.setMargins(16, 16, 16, 16);
		ll.addView(sv);
		sv.setLayoutParams(lp);
		
		setContentView(ll);
    }
}
