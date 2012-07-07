package org.ling0322.danci;

import java.io.File;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

public class LiaPreferencesActivity 
    extends PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener {
    
    private Preference mWordlistPref;
    private Preference mRecitingRestartPref;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mWordlistPref = findPreference("wordlist");
        mWordlistPref.setOnPreferenceChangeListener(this);
        mRecitingRestartPref = findPreference("reciting_restart");
        mRecitingRestartPref.setOnPreferenceClickListener(this);
    }
    
    public void setNeedsRefreshResult() {
        Intent result = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("bNeedsRefresh", true);
        result.putExtras(b);
        setResult(Activity.RESULT_OK, result);        
    }

    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        setNeedsRefreshResult();
        return true;
    }

    public boolean onPreferenceClick(Preference arg0) {
        setNeedsRefreshResult();
        return true;
    }
} 
