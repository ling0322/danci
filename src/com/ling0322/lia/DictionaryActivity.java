package com.ling0322.lia;

import java.io.File;
import java.util.ArrayList;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.database.sqlite.*;
import android.support.v4.app.*;


public class DictionaryActivity extends Fragment implements TextWatcher, OnItemClickListener {
	
    public ArrayAdapter<String> adapter;
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
        
        EditText et = (EditText)getActivity().findViewById(R.id.editText1);
        et.addTextChangedListener(this);
        wordlist =  new ArrayList<String>();
        
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, wordlist);
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


}
