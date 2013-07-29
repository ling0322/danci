package org.ling0322.danci;

 
import java.util.ArrayList;
import java.util.Date;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.*;
import android.preference.PreferenceManager;
import android.util.*;

public class WordlistDB {
    public static final int REVIEW_TIMES = 4;

    public int getProgress() {
        String sqlCmd;
        Cursor c;
        SQLiteDatabase conn = Wordlist.openWordlistDb();
        sqlCmd = "select count(*) from dict where tested = 0";
        c = conn.rawQuery(sqlCmd, null);
        c.moveToFirst();
        int nNewWord = c.getInt(0);
        c.close();
        
        sqlCmd = String.format(
            "select count(*) from dict where tested != correct and continuous_correct < %d",
            REVIEW_TIMES);
        c = conn.rawQuery(sqlCmd, null);
        c.moveToFirst();
        int nReviewWord = c.getInt(0);
        c.close();
        conn.close();
        return nNewWord + nReviewWord;
    }
  
    public int reviewCount() {
        String sqlCmd;
        Cursor c;
        Date d = new Date();
        long cntSecond = d.getTime() / 1000;
        SQLiteDatabase conn = Wordlist.openWordlistDb();
        
        sqlCmd = String.format(
            "select count(*) from dict where tested != correct and continuous_correct < %d and last_tested / (60 * 24 * 60) != %d",
            REVIEW_TIMES,
            (int)(cntSecond / (24 * 60 * 60)));
        c = conn.rawQuery(sqlCmd, null);
        c.moveToFirst();
        int nReviewWord = c.getInt(0);
        c.close();
        conn.close();
        return nReviewWord;
    }        

    public int reviewCountAll() {
        String sqlCmd;
        Cursor c;
        sqlCmd = String.format(
            "select count(*) from dict where tested != correct and continuous_correct < %d",
            REVIEW_TIMES);
        SQLiteDatabase conn = Wordlist.openWordlistDb();
        c = conn.rawQuery(sqlCmd, null);
        c.moveToFirst();
        int nReviewWord = c.getInt(0);
        c.close();
        conn.close();
        return nReviewWord;
    }        
    
    public Pair<ArrayList<String>, ArrayList<Integer>> reviewListAll(int wordPerPage, int page) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Config.mainInstance);
        boolean mIsQuickMode = sp.getBoolean("quickLearning", false);
        int step = mIsQuickMode? 2: 1;
        String sqlCmd;
        sqlCmd = String.format(
            "select word, continuous_correct from dict where tested != correct and continuous_correct < %d limit %d offset %d",
            REVIEW_TIMES,
            wordPerPage,
            wordPerPage * page);
        SQLiteDatabase conn = Wordlist.openWordlistDb();
        Cursor c = conn.rawQuery(sqlCmd, null);
        ArrayList<String> reviewWords = new ArrayList<String>();
        ArrayList<Integer> remainsList = new ArrayList<Integer>();
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); ++i) {
            reviewWords.add(c.getString(0));
            remainsList.add((int)Math.ceil(
                ((REVIEW_TIMES - c.getInt(1)) / (double)step)));
            c.moveToNext();
        }
        c.close();
        conn.close();
        return new Pair<ArrayList<String>, ArrayList<Integer>>(reviewWords, remainsList);        
    }
    
    public ArrayList<String> reviewList(int numWord) {
        
        Date d = new Date();
        long cntSecond = d.getTime() / 1000;
        String sqlCmd;
        if (Wordlist.isRandomOrder() == true) {
            sqlCmd = "select word from dict where tested != correct and continuous_correct < %d and last_tested / (60 * 24 * 60) != %d order by random() limit %d";
        } else {
            sqlCmd = "select word from dict where tested != correct and continuous_correct < %d and last_tested / (60 * 24 * 60) != %d order by word limit %d";
        }
        SQLiteDatabase conn = Wordlist.openWordlistDb();
        Cursor c = conn.rawQuery(
            String.format(sqlCmd, REVIEW_TIMES, cntSecond / (24 * 60 * 60), numWord), null);
        ArrayList<String> reviewWords = new ArrayList<String>();
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); ++i) {
            reviewWords.add(c.getString(0));
            c.moveToNext();
        }
        c.close();
        conn.close();
        return reviewWords;
    }

    public ArrayList<String> newWordList(int limit) {
        String sqlCmd;
        SQLiteDatabase conn = Wordlist.openWordlistDb();
    
        if (Wordlist.isRandomOrder() == true) {
            sqlCmd = String.format("select word from dict where tested = 0 order by random() limit %d", limit);
        } else {
            sqlCmd = String.format("select word from dict where tested = 0 order by word limit %d", limit);
        }
        Cursor c = conn.rawQuery(sqlCmd, null);
        c.moveToFirst();
        ArrayList<String> newWords = new ArrayList<String>();
        for (int i = 0; i < c.getCount(); ++i) {
            newWords.add(c.getString(0));
            c.moveToNext();
        }
        c.close();
        conn.close();
        return newWords;
    }
    
    public void answerList(ArrayList<Pair<String, Boolean>> answerList) {
        Log.d("wldb", "answerList called");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Config.mainInstance);
        boolean mIsQuickMode = sp.getBoolean("quickLearning", false);
        int step = mIsQuickMode ? 2: 1;
        SQLiteDatabase conn = Wordlist.openWordlistDb();
        conn.beginTransaction();
        String cmd;
        Date d = new Date();
        long cntSecond = d.getTime() / 1000;
        try {
            for (Pair<String, Boolean> one : answerList) {
                if (one.second == true) {
                    cmd = "update dict set tested = tested + ?, correct = correct + ?, continuous_correct = continuous_correct + ?, last_tested = ? where word = ?";
                    conn.execSQL(cmd, new Object[] {step, step, step, cntSecond, one.first});
                } else {
                    cmd = "update dict set tested = tested + ?, continuous_correct = max(0, continuous_correct - ?), last_tested = ? where word = ?";
                    conn.execSQL(cmd, new Object[] {step, step, cntSecond, one.first});
                }
                Log.d("lia", "sql update command");
            }
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
            conn.close();
        }
    }
    
    public int totalWord() {
        int totalWord;
        SQLiteDatabase conn = Wordlist.openWordlistDb();
        String sqlCmd = "select count(*) from dict";
        Cursor c = conn.rawQuery(sqlCmd, null);
        c.moveToFirst();
        totalWord = c.getInt(0);
        c.close();
        conn.close();
        return totalWord;
    }
    
    //
    // if not use static variable mIsNullDb, it may case database lock exception when update 
    // reciting result in the update thread and call this function in the main thread  
    //
    private static boolean mIsNullDb = true;
    public boolean isNullDbConn() {
        if (mIsNullDb == false)
            return false;
        SQLiteDatabase conn = Wordlist.openWordlistDb();
        if (conn == null)
            return true;
        
        mIsNullDb = false;
        conn.close();
        return false;
    }
}
