package com.ling0322.lia;

import java.io.File;

import android.content.*;
import android.os.Bundle;
import android.preference.*;

public class LiaPreferencesActivity 
    extends PreferenceActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences); 
        File speech = new File(Lia.SPEECH_PATH.concat("/A"));
        if (speech.exists() == false) {
        	Preference pSpeech = findPreference("auto_speech");
        	pSpeech.setEnabled(false);
        }
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    	
    	Lia.mainInstance.finish();
    	Intent it = new Intent(this, LiaActivity.class);
    	startActivity(it);
    }
} 
