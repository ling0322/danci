package org.ling0322.danci;

import java.io.File;
import java.util.*;

import android.database.*;
import android.database.sqlite.*;

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
		}
        if (isDictExist(Config.DICT_FULL_PATH)) {
            dictDbList.add(SQLiteDatabase.openOrCreateDatabase(Config.DICT_FULL_PATH, null));
        }
        if (isDictExist(Config.DICT_EC_PATH)) {
            dictDbList.add(SQLiteDatabase.openOrCreateDatabase(Config.DICT_EC_PATH, null));
        }
	}
	
	public static Dictionary getInstance() {
		if (instance == null) {
		    instance = new Dictionary();
		}
		return instance;
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
	
	public String getDefinition(String word) {
	    for (SQLiteDatabase dbDict : dictDbList) {
	        String defi = getDefinitionFromDict(dbDict, word);
	        if (defi != null)
	            return defi;
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
	    return new ArrayList<String>(wordList.subList(0, 30));
	    
	}
	

}
