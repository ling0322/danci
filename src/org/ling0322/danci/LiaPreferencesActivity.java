package org.ling0322.danci;

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
        File speech = new File(Config.SPEECH_PATH.concat("/A"));
        if (speech.exists() == false) {
        	Preference pSpeech = findPreference("auto_speech");
        	pSpeech.setEnabled(false);
        }
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    	
    	Config.mainInstance.finish();
    	Intent it = new Intent(this, MainActivity.class);
    	startActivity(it);
    }
} 
