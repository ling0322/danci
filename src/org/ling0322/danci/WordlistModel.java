package org.ling0322.danci;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.*;

public class WordlistModel {
    public WordlistModel(Activity activity) {
        mActivity = activity;
    }
    
    public static final int REVIEW_TIMES = 4;
    private Activity mActivity;
    


    public int getProgress() {
        String sqlCmd;
        Cursor c;
        SQLiteDatabase conn = Wordlist.openWordlistDb(mActivity);
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
        SQLiteDatabase conn = Wordlist.openWordlistDb(mActivity);
        
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
        SQLiteDatabase conn = Wordlist.openWordlistDb(mActivity);
        c = conn.rawQuery(sqlCmd, null);
        c.moveToFirst();
        int nReviewWord = c.getInt(0);
        c.close();
        conn.close();
        return nReviewWord;
    }        
    
    public Pair<ArrayList<String>, ArrayList<Integer>> reviewListAll(int wordPerPage, int page) {
        String sqlCmd;
        sqlCmd = String.format(
            "select word, continuous_correct from dict where tested != correct and continuous_correct < %d limit %d offset %d",
            REVIEW_TIMES,
            wordPerPage,
            wordPerPage * page);
        SQLiteDatabase conn = Wordlist.openWordlistDb(mActivity);
        Cursor c = conn.rawQuery(sqlCmd, null);
        ArrayList<String> reviewWords = new ArrayList<String>();
        ArrayList<Integer> remainsList = new ArrayList<Integer>();
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); ++i) {
            reviewWords.add(c.getString(0));
            remainsList.add(c.getInt(1));
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
        if (Wordlist.isRandomOrder(mActivity) == true) {
            sqlCmd = "select word from dict where tested != correct and continuous_correct < %d and last_tested / (60 * 24 * 60) != %d order by random() limit %d";
        } else {
            sqlCmd = "select word from dict where tested != correct and continuous_correct < %d and last_tested / (60 * 24 * 60) != %d order by word limit %d";
        }
        SQLiteDatabase conn = Wordlist.openWordlistDb(mActivity);
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
        SQLiteDatabase conn = Wordlist.openWordlistDb(mActivity);
    
        if (Wordlist.isRandomOrder(mActivity) == true) {
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
        SQLiteDatabase conn = Wordlist.openWordlistDb(mActivity);
        conn.beginTransaction();
        String cmd;
        Date d = new Date();
        long cntSecond = d.getTime() / 1000;
        try {
            for (Pair<String, Boolean> one : answerList) {
                if (one.second == true) {
                    cmd = "update dict set tested = tested + 1, correct = correct + 1, continuous_correct = continuous_correct + 1, last_tested = ? where word = ?";
                } else {
                    cmd = "update dict set tested = tested + 1, continuous_correct = max(0, continuous_correct - 1), last_tested = ? where word = ?";
                }
                conn.execSQL(cmd, new Object[] {cntSecond, one.first});
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
        SQLiteDatabase conn = Wordlist.openWordlistDb(mActivity);
        String sqlCmd = "select count(*) from dict";
        Cursor c = conn.rawQuery(sqlCmd, null);
        c.moveToFirst();
        totalWord = c.getInt(0);
        c.close();
        conn.close();
        return totalWord;
    }
    
    public boolean isNullDbConn() {
        SQLiteDatabase conn = Wordlist.openWordlistDb(mActivity);
        if (conn == null)
            return true;
        
        conn.close();
        return false;
    }
}
