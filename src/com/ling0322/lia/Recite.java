package com.ling0322.lia;

import java.util.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;

class UpdateDatabase implements Runnable {
	private ArrayList<String> sqlUpdateBuffer;
	private SQLiteDatabase conn;
    public UpdateDatabase(ArrayList<String> sqlUpdateBuffer, SQLiteDatabase conn) {
    	this.conn = conn;
    	this.sqlUpdateBuffer = sqlUpdateBuffer;
    }
	public void run() {
		conn.beginTransaction();
		try {
		    for (String sql : sqlUpdateBuffer) {
		        conn.execSQL(sql);
		        Log.d("lia", "sql update command");
		    }
		    conn.setTransactionSuccessful();
		} finally {
		    conn.endTransaction();
		}
	}
	
}

public class Recite {

    public final int NEW_WORD_PER_UNIT = 30;
    public final int REVIEW_WORD_PER_UNIT = 30;
    
    private final int ST_FINISHED = 0;
    private final int ST_NEW = 1;
    private final int ST_REVIEW = 2;
    private final int ST_DRILL = 3;
    
    private SQLiteDatabase dbsqlite;
    private Thread updateThread;
    private ArrayList<String> wordList = new ArrayList<String>();
    private ArrayList<String> drillList = new ArrayList<String>();
    private ArrayList<String> sqlUpdateBuffer= new ArrayList<String>();
    private int cntState = ST_FINISHED;
    private String cntWord = "";
    private int totalWord = -1;
    private int newWordCount = 0;
    private int reviewWordCount = 0;
    

	public Recite() {
		String sqlCmd;
		Cursor c;
		dbsqlite = Wordlist.getWordlistDbConn();
		if (dbsqlite == null)
			return ;
		sqlCmd = "select count(*) from dict";
		c = dbsqlite.rawQuery(sqlCmd, null);
		c.moveToFirst();
		totalWord = c.getInt(0);
		c.close();
	}
	
	public boolean isNullDbConn() {
		if (dbsqlite == null)
			return true;
		return false;
	}
	
	public void finallize() {
		dbsqlite.close();
	}
	
	private ArrayList<String> getNewWordList(int limit) {
		String sqlCmd;
	
		if (Wordlist.isRandomOrder() == true) {
			sqlCmd = String.format("select word from dict where tested = 0 order by random() limit %d", limit);
		} else {
			sqlCmd = String.format("select word from dict where tested = 0 order by word limit %d", limit);
		}
		Cursor c = dbsqlite.rawQuery(sqlCmd, null);
		c.moveToFirst();
		ArrayList<String> newWords = new ArrayList<String>();
        for (int i = 0; i < c.getCount(); ++i) {
        	newWords.add(c.getString(0));
        	c.moveToNext();
        }
        c.close();
        return newWords;
	}
	
	private ArrayList<String> getReviewWordList() {
		
		Date d = new Date();
		long cntSecond = d.getTime() / 1000;
		String sqlCmd;
		if (Wordlist.isRandomOrder() == true) {
			sqlCmd = "select word from dict where tested != correct and continuous_correct != 4 and last_tested / (60 * 24 * 60) != %d order by random() limit %d";
		} else {
			sqlCmd = "select word from dict where tested != correct and continuous_correct != 4 and last_tested / (60 * 24 * 60) != %d order by word limit %d";
		}
		Cursor c = dbsqlite.rawQuery(String.format(sqlCmd, cntSecond / (24 * 60 * 60), REVIEW_WORD_PER_UNIT), null);
		ArrayList<String> reviewWords = new ArrayList<String>();
		c.moveToFirst();
		for (int i = 0; i < c.getCount(); ++i) {
			reviewWords.add(c.getString(0));
        	c.moveToNext();
        }
		c.close();
        return reviewWords;
	}
	
	public int getProgress() {
		String sqlCmd;
		Cursor c;
		
		sqlCmd = "select count(*) from dict where tested = 0";
		c = dbsqlite.rawQuery(sqlCmd, null);
		c.moveToFirst();
		int nNewWord = c.getInt(0);
		c.close();
		
		sqlCmd = "select count(*) from dict where tested != correct and continuous_correct != 4";
		c = dbsqlite.rawQuery(sqlCmd, null);
		c.moveToFirst();
		int nReviewWord = c.getInt(0);
		c.close();
		return nNewWord + nReviewWord;
	}
	
	public String getCntState() {
		switch (cntState) {
		case ST_FINISHED:
			int unpass = getProgress();
			String cntwl = Wordlist.currentWordList();
			return String.format(
				"%s→当前进度: %d/%d %d%%",
				cntwl,
				totalWord - unpass,
				totalWord,
				(int)((totalWord - unpass) * 100 / totalWord)
				);
		case ST_NEW:
			return String.format("开始→[新单词 - %d/%d]→复习→巩固", wordList.size(), newWordCount);
		case ST_REVIEW:
			return String.format("开始→新单词→[复习 - %d/%d]→巩固", wordList.size(), reviewWordCount);
		case ST_DRILL:
			return String.format("开始→新单词→复习→[巩固 - %d]", wordList.size());
		}
		
		return "";
	}
	
	private int getReviewWordNumber() {
		String sqlCmd;
		Cursor c;
		Date d = new Date();
		long cntSecond = d.getTime() / 1000;
		
		sqlCmd = "select count(*) from dict where tested != correct and continuous_correct != 4 and last_tested / (60 * 24 * 60) != %d";
		c = dbsqlite.rawQuery(String.format(sqlCmd, (int)(cntSecond / (24 * 60 * 60))), null);
		c.moveToFirst();
		int nReviewWord = c.getInt(0);
		c.close();
		return nReviewWord;
	}
	
	public void finish() {
	    cntState = ST_FINISHED;
	}
	
	public void start() {
		int reviewWords = getReviewWordNumber();
		if (cntState == ST_FINISHED) {
			cntState = ST_NEW;
			wordList.clear();
			if (reviewWords < 300) {
				wordList = getNewWordList(NEW_WORD_PER_UNIT - (int)(reviewWords / 10));
				newWordCount = wordList.size();
			}
			switchState();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void switchState() {
		if (wordList.size() == 0) {
			switch(cntState) {
			case ST_NEW:
				wordList = getReviewWordList();
				reviewWordCount = wordList.size();
				cntState = ST_REVIEW;
				switchState();
				break;
			case ST_REVIEW:
				wordList = drillList;
				cntState = ST_DRILL;
				//
				// During the drill period, there is no database IO, so we could
				// now start to write back to database
				//
				updateThread = new Thread(
					new UpdateDatabase((ArrayList<String>) sqlUpdateBuffer.clone(), dbsqlite)
					);
				updateThread.start();
				sqlUpdateBuffer.clear();
				switchState();
				break;
			case ST_DRILL:
				cntState = ST_FINISHED;
				//
				// Here, we start to use database IO again, so we should wait updateThread
				// to finish HERE
				//
				try {
					updateThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				drillList.clear();
				wordList.clear();
			}
		}
	}
	
	public String pickWord() {
		cntWord = wordList.get(0);
		return cntWord;
	}
	
	public String pickDefinition() {
		String sqlCmd = "select definition from dict where word = '%s'";
		Cursor c = dbsqlite.rawQuery(String.format(sqlCmd, cntWord), null);
		c.moveToFirst();
		String d = c.getString(0);
		c.close();
		return d;
	}
	
	public boolean isFinsihed() {
		if (cntState == ST_FINISHED) {
			return true;
		} else {
			return false;
		}
	}
	
	private void updateSql(String sqlCmd, boolean forceWrite) {
		sqlUpdateBuffer.add(sqlCmd);
	}
	
	public void answer(boolean correct) {
		String sqlCmd;
		Date d = new Date();
		long cntSecond = d.getTime() / 1000;
		
		if (wordList.size() == 0)
			return ;
		
		if (cntState == ST_DRILL) {
			wordList.remove(0);
			if (correct == false) {
				wordList.add(cntWord);
			}
			switchState();
		} else {
			wordList.remove(0);
			if (correct == true) {
				sqlCmd = "update dict set tested = tested + 1, correct = correct + 1, continuous_correct = continuous_correct + 1, last_tested = %d where word = '%s'";
			} else {
				drillList.add(cntWord);
                sqlCmd = "update dict set tested = tested + 1, continuous_correct = max(0, continuous_correct - 1), last_tested = %d where word = '%s'";
			}
			updateSql(String.format(sqlCmd, cntSecond, cntWord), false);
			switchState();
		}
	}
}
