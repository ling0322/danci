package com.ling0322.lia;

import java.util.HashMap;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class Wordlist {
	public static HashMap<String, SQLiteDatabase> wordlistDbConnMap = new HashMap<String, SQLiteDatabase>();
	public static SQLiteDatabase getWordlistDbConn() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Lia.mainInstance);
		String wordlistName = sp.getString("wordlist", "");
		if (wordlistName.equals(""))
			return null;
		if (wordlistName.equals("英语四级(CET-4)")) {
			if (wordlistDbConnMap.containsKey(wordlistName) == false)
				wordlistDbConnMap.put(wordlistName, SQLiteDatabase.openOrCreateDatabase(Lia.WL_CET4_PATH, null));
			return wordlistDbConnMap.get(wordlistName);
		}
		
		if (wordlistName.equals("英语六级(CET-6)")) {
			if (wordlistDbConnMap.containsKey(wordlistName) == false)
				wordlistDbConnMap.put(wordlistName, SQLiteDatabase.openOrCreateDatabase(Lia.WL_CET6_PATH, null));
			return wordlistDbConnMap.get(wordlistName);
		}
		
		if (wordlistName.equals("考研英语词汇")) {
			if (wordlistDbConnMap.containsKey(wordlistName) == false)
				wordlistDbConnMap.put(wordlistName, SQLiteDatabase.openOrCreateDatabase(Lia.WL_KAOYAN_PATH, null));
			return wordlistDbConnMap.get(wordlistName);
		}
		
		if (wordlistName.equals("雅思精选词汇")) {
			if (wordlistDbConnMap.containsKey(wordlistName) == false)
				wordlistDbConnMap.put(wordlistName, SQLiteDatabase.openOrCreateDatabase(Lia.WL_IELTS_PATH, null));
			return wordlistDbConnMap.get(wordlistName);
		}
		
		if (wordlistName.equals("TOFEL词汇")) {
			if (wordlistDbConnMap.containsKey(wordlistName) == false)
				wordlistDbConnMap.put(wordlistName, SQLiteDatabase.openOrCreateDatabase(Lia.WL_TOFEL_PATH, null));
			return wordlistDbConnMap.get(wordlistName);
		}
		
		if (wordlistName.equals("GRE词汇")) {
			if (wordlistDbConnMap.containsKey(wordlistName) == false)
				wordlistDbConnMap.put(wordlistName, SQLiteDatabase.openOrCreateDatabase(Lia.WL_GRE_PATH, null));
			return wordlistDbConnMap.get(wordlistName);
		}
		return null;
	}
	
	public static String currentWordList() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Lia.mainInstance);
		return sp.getString("wordlist", "");		
	}
	
	public static boolean isRandomOrder() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Lia.mainInstance);
		return sp.getBoolean("random_order", true);
	}

}
