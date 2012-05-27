package org.ling0322.danci;

import java.util.ArrayList;
import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.*;
import android.util.*;

public class WordlistDB {
    private SQLiteDatabase conn;
    public WordlistDB(SQLiteDatabase conn) {
	this.conn = conn;
    }

    public int getProgress() {
	String sqlCmd;
	Cursor c;
	
	sqlCmd = "select count(*) from dict where tested = 0";
	c = conn.rawQuery(sqlCmd, null);
	c.moveToFirst();
	int nNewWord = c.getInt(0);
	c.close();
	
	sqlCmd = "select count(*) from dict where tested != correct and continuous_correct != 4";
	c = conn.rawQuery(sqlCmd, null);
	c.moveToFirst();
	int nReviewWord = c.getInt(0);
	c.close();
	return nNewWord + nReviewWord;
    }
    
    public boolean isNullDbConn() {
    	if (conn == null)
    		return true;
    	else
    		return false;
    }
	
    public int reviewCount() {
        String sqlCmd;
        Cursor c;
        Date d = new Date();
        long cntSecond = d.getTime() / 1000;
        
        sqlCmd = "select count(*) from dict where tested != correct and continuous_correct != 4 and last_tested / (60 * 24 * 60) != %d";
        c = conn.rawQuery(String.format(sqlCmd, (int)(cntSecond / (24 * 60 * 60))), null);
        c.moveToFirst();
        int nReviewWord = c.getInt(0);
        c.close();
        return nReviewWord;
    }	

    public int reviewCountAll() {
        String sqlCmd;
        Cursor c;
        sqlCmd = "select count(*) from dict where tested != correct and continuous_correct != 4";
        c = conn.rawQuery(sqlCmd, null);
        c.moveToFirst();
        int nReviewWord = c.getInt(0);
        c.close();
        return nReviewWord;
    }	
    
    public ArrayList<String> reviewListAll(int wordPerPage, int page) {
        String sqlCmd;
        sqlCmd = "select word from dict where tested != correct and continuous_correct != 4 limit %d offset %d";
        Cursor c = conn.rawQuery(String.format(sqlCmd, wordPerPage, wordPerPage * page), null);
        ArrayList<String> reviewWords = new ArrayList<String>();
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); ++i) {
            reviewWords.add(c.getString(0));
            c.moveToNext();
        }
        c.close();
        return reviewWords;	
    }
    
    public ArrayList<String> reviewList(int numWord) {
        
        Date d = new Date();
        long cntSecond = d.getTime() / 1000;
        String sqlCmd;
        if (Wordlist.isRandomOrder() == true) {
            sqlCmd = "select word from dict where tested != correct and continuous_correct != 4 and last_tested / (60 * 24 * 60) != %d order by random() limit %d";
        } else {
            sqlCmd = "select word from dict where tested != correct and continuous_correct != 4 and last_tested / (60 * 24 * 60) != %d order by word limit %d";
        }
        Cursor c = conn.rawQuery(String.format(sqlCmd, cntSecond / (24 * 60 * 60), numWord), null);
        ArrayList<String> reviewWords = new ArrayList<String>();
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); ++i) {
            reviewWords.add(c.getString(0));
            c.moveToNext();
        }
        c.close();
        return reviewWords;
    }

    public ArrayList<String> newWordList(int limit) {
        String sqlCmd;
    
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
        return newWords;
    }
    
    public void answerList(ArrayList<Pair<String, Boolean>> answerList) {
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
        }
    }
    
    public int totalWord() {
	int totalWord;
        String sqlCmd = "select count(*) from dict";
        Cursor c = conn.rawQuery(sqlCmd, null);
        c.moveToFirst();
        totalWord = c.getInt(0);
        c.close();
        return totalWord;
    }
}
