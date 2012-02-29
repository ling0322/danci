package com.ling0322.lia;

import java.util.ArrayList;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.support.v4.app.*;


public class DictionaryActivity 
    extends Fragment 
    implements TextWatcher, OnItemClickListener, OnClickListener, OnFocusChangeListener {
	
    public WordlistAdapter adapter;
    private EditText et = null;
    public ArrayList<String> wordlist;
    private Dict dict12;

	private void updateWordList(String word) {
		if (word.equals("")) {
			wordlist.clear();
	        adapter.notifyDataSetChanged();
	        return ;
		}

		wordlist.clear();
        wordlist.addAll(dict12.getWordList(word));
        adapter.notifyDataSetChanged();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	//
    	// get the OxfordJm Dict Object
    	//
    	dict12 = Dict.getInstance(Dict.DICT_12);
    	return inflater.inflate(R.layout.dict, container, false);
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        ListView lv = (ListView)getActivity().findViewById(R.id.listView1);
        
        et = (EditText)getActivity().findViewById(R.id.editText1);
        et.addTextChangedListener(this);
        
        //
        // let et select all when user click this EditText 
        //
        et.setOnClickListener(this);
        et.setOnFocusChangeListener(this);
        wordlist =  new ArrayList<String>();
        
        adapter = new WordlistAdapter(wordlist, getActivity());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }


	public void afterTextChanged(Editable arg0) { }
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		updateWordList(arg0.toString());
	}


	public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
		//
		// start the DefinitionActivity
		//
		Intent it = new Intent(getActivity(), DefinitionActivity.class);
		it.putExtra("word", wordlist.get(position));
		getActivity().startActivity(it);
		
	}


	public void onFocusChange(View view, boolean b) {
		if (view == et && b == true)
			et.selectAll();
	}

	public void onClick(View view) {
		if (view == et) {
			et.selectAll();
		}
		
	}


}
