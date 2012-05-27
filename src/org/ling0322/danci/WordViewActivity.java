package org.ling0322.danci;

import java.util.*;
import android.app.*;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WordViewActivity extends Activity {

}

class DefinitionFragment extends Fragment {
    private String word;
    private Activity activity;
    
    public DefinitionFragment(Activity activity, String word) {
        this.word = word;
        this.activity = activity;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return DefinitionView.getDefinitionView(activity, word);
    }
}

class WordFragmentAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> wordlist;
    private Activity activity;

    public WordFragmentAdapter(FragmentManager fm, Activity activity, ArrayList<String> wordlist) {
	super(fm);
	this.wordlist = wordlist;
	this.activity = activity;
    }

    @Override
    public Fragment getItem(int arg0) {
	return new DefinitionFragment(activity, wordlist.get(arg0));
    }

    @Override
    public int getCount() {
	return wordlist.size();
    }

}