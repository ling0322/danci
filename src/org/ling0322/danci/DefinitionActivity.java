package org.ling0322.danci;


import android.app.*;
import android.os.*;
import android.view.*;
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
