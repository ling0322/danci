package com.ling0322.lia;

import java.util.*;

import android.app.Activity;
import android.database.*;
import android.database.sqlite.*;

public class Dict {
	private SQLiteDatabase db;
	public static final int DICT_OXFORD = 0;
	public static final int DICT_OXFORDJM = 1;
	public static final int DICT_12 = 2;
	public static final int COUNT = 3;
	private static Dict[] dictInstance = new Dict[COUNT];

	private Dict(int dictId) {
		switch (dictId) {
		case DICT_OXFORD:
			db = null;
			break;
		case DICT_OXFORDJM:
			db = null;
			break;
		case DICT_12:
			db = SQLiteDatabase.openOrCreateDatabase(Lia.DICT_12_PATH, null);
			break;
		}
	}
	public static Dict getInstance(int dictId) {
		if (dictInstance[dictId] == null) {
			dictInstance[dictId] = new Dict(dictId);
		}
		return dictInstance[dictId];
	}
	
	public String getDefinition(String word) {
		String result;
		Cursor c = db.rawQuery(
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
	
	public ArrayList<String> getWordList(String word) {
		ArrayList<String> result = new ArrayList<String>();
        Cursor c = db.rawQuery(
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

}
