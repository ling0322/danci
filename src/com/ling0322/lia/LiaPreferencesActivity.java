package com.ling0322.lia;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class LiaPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences); 
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    	Lia.mainInstance.finish();
    	Intent it = new Intent(this, LiaActivity.class);
    	startActivity(it);
    }
}
