package org.ling0322.danci;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class Wordlist {
    public static HashMap<String, SQLiteDatabase> wordlistDbConnMap = new HashMap<String, SQLiteDatabase>();
    public static SQLiteDatabase openWordlistDb(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String wordlistName = sp.getString("wordlist", "");
        if (wordlistName.equals(""))
            return null;
        
        if (wordlistName.equals("英语四级(CET-4)")) {
            return SQLiteDatabase.openOrCreateDatabase(Config.WL_CET4_PATH, null);
        }
        
        if (wordlistName.equals("英语六级(CET-6)")) {
            return SQLiteDatabase.openOrCreateDatabase(Config.WL_CET6_PATH, null);
        }
        
        if (wordlistName.equals("考研英语词汇")) {
            return SQLiteDatabase.openOrCreateDatabase(Config.WL_KAOYAN_PATH, null);
        }
        
        if (wordlistName.equals("雅思精选词汇")) {
            return SQLiteDatabase.openOrCreateDatabase(Config.WL_IELTS_PATH, null);
        }
        
        if (wordlistName.equals("TOFEL词汇")) {
            return SQLiteDatabase.openOrCreateDatabase(Config.WL_TOFEL_PATH, null);
        }
        
        if (wordlistName.equals("GRE词汇")) {
            return SQLiteDatabase.openOrCreateDatabase(Config.WL_GRE_PATH, null);
        }
        return null;
    }
    
    public static String currentWordList(Activity activity) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString("wordlist", "");        
    }
    
    public static boolean isRandomOrder(Activity activity) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getBoolean("random_order", true);
    }

}
