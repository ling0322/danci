package org.ling0322.danci;

import java.io.File;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import android.database.*;
import android.database.sqlite.*;
import android.util.Log;

public class Dictionary {
	private ArrayList<SQLiteDatabase> dictDbList;
	private static Dictionary instance = null;

	private boolean isDictExist(String dictPath) {
	    File file = new File(dictPath);
	    return file.exists();
	}
	
	private Dictionary() {
	    dictDbList = new ArrayList<SQLiteDatabase>();
		if (isDictExist(Config.DICT_12_PATH)) {
		    dictDbList.add(SQLiteDatabase.openOrCreateDatabase(Config.DICT_12_PATH, null));
		    Log.d("Dictionary", "added dict-12");
		}
        if (isDictExist(Config.DICT_FULL_PATH)) {
            dictDbList.add(SQLiteDatabase.openOrCreateDatabase(Config.DICT_FULL_PATH, null));
            Log.d("Dictionary", "added dict-full");
        }
        if (isDictExist(Config.DICT_EC_PATH)) {
            dictDbList.add(SQLiteDatabase.openOrCreateDatabase(Config.DICT_EC_PATH, null));
            Log.d("Dictionary", "added dict-ec");
        }
	}
	
	public static Dictionary getInstance() {
		if (instance == null) {
		    instance = new Dictionary();
		}
		return instance;
	}
	
	public static class Word {
	    public String word;
	    public String pron;
	    public ArrayList<String> definitions;
	    public ArrayList<String> examplesOrig;
	    public ArrayList<String> examplesTrans;
	}
	
	private String getDefinitionFromDict(SQLiteDatabase dbDict, String word) {
		String result;
		Cursor c = dbDict.rawQuery(
			"select definition from dict where word == ?", 
			new String[] { word });
		
		
        if (c.getCount() == 0) {
        	return null;
        } else {
        	c.moveToFirst();
        	result = c.getString(0);
        }
    	c.close();
    	return result;
	}
	
	private Word jsonDefiToWord(String word, String jsonDefi) {
        Word dictWord = new Word();
        try {
            dictWord.word = word;
            JSONObject json = new JSONObject(jsonDefi.toLowerCase());
            dictWord.pron = json.getString("pron");
            
            dictWord.definitions = new ArrayList<String>();
            for (String defi : json.getString("def").split("\\\\n")) {
                dictWord.definitions.add(defi);
            }
            
            JSONArray jsonExamples = json.getJSONArray("example");
            dictWord.examplesOrig = new ArrayList<String>();
            dictWord.examplesTrans = new ArrayList<String>();
            for (int i = 0; i < jsonExamples.length(); ++i) {
                JSONObject jsonExample = jsonExamples.getJSONObject(i);
                dictWord.examplesOrig.add(jsonExample.getString("orig"));
                dictWord.examplesTrans.add(jsonExample.getString("trans"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dictWord;
	}
	
	public Word getDefinition(String word) {
	    for (SQLiteDatabase dbDict : dictDbList) {
	        String defiJson = getDefinitionFromDict(dbDict, word);
	        if (defiJson != null) {
	            return jsonDefiToWord(word, defiJson);
	        }
	    }
	    return null;
	}
	
	private ArrayList<String> getWordListFromDict(SQLiteDatabase dbDict, String word) {
		ArrayList<String> result = new ArrayList<String>();
        Cursor c = dbDict.rawQuery(
        	"select word from dict where word >= ? order by word limit 30", 
        	new String[] { word });

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); ++i) {
        	result.add(c.getString(0));
        	c.moveToNext();
        }
        c.close();
        return result;
	}
	
	private void singleton(ArrayList<String> list) {
	    int i = 0;
	    String lastWord = "";
	    while (i < list.size()) {
	        if (true == lastWord.equals(list.get(i))) {
	            list.remove(i);
	        } else {
	            lastWord = list.get(i);
	            i++;	            
	        }
	    }
	}
	
	public ArrayList<String> getWordList(String word) {
	    ArrayList<String> wordList = new ArrayList<String>();
	    for (SQLiteDatabase dbDict : dictDbList) {
	        wordList.addAll(getWordListFromDict(dbDict, word));
	    }
	    Collections.sort(wordList);
	    singleton(wordList);
	    if (wordList.size() <= 30)
	        return wordList;
	    
	    return new ArrayList<String>(wordList.subList(0, 30));
	    
	}
	

}
