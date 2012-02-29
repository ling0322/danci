package com.ling0322.lia;

import java.io.*;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

class CheckUsefulFiles implements Runnable {
    private Activity activity;
    private Handler handler;
    public CheckUsefulFiles(Activity activity, Handler handler) {
    	this.activity = activity;
    	this.handler = handler;
    }
    
    public void setupFile(String dest, String from) throws Exception {
	    File fp = new File(dest);
    	if (fp.exists() == false || fp.length() < 10 * 1024) {
    		OutputStream out = new FileOutputStream(fp);
    		InputStream in = activity.getAssets().open(from);
    		byte [] buf = new byte[8192];
    		sendDisplayMessage(String.format("少女祈祷中, 请稍等 %s", from));
    		while (-1 != in.read(buf)) {
    			out.write(buf);
    		}
    		in.close();
    		out.close();
    	}
    }
    public void setupDict() throws Exception {
    	//
    	// first delete this file
    	//
    	File f = new File(Lia.DICT_12_PATH);
    	if (f.exists() == true && f.length() > 1 * 1024 * 1024)
    		return ;
    	
    	if (f.exists() == true && f.length() < 1 * 1024 * 1024)
    		f.delete();

    	
    	SQLiteDatabase conn = SQLiteDatabase.openOrCreateDatabase(Lia.DICT_12_PATH, null);
		conn.beginTransaction();
		try {
			conn.execSQL("create table dict(word text primary key, definition text)");
		    for (int i = 0; i < Lia.DICT_12_PARTS; ++i) {
			    BufferedReader in = new BufferedReader(new InputStreamReader(activity.getAssets().open("dict-12-v2.part".concat(Integer.toString(i)))));
		        String line;
			    while (null != (line = in.readLine())) {
		        	String[] sp = line.split(" :: ");
		        	conn.execSQL(
		        		"insert into dict(word, definition) values(?, ?)",
		        		sp);
		        }
			    sendDisplayMessage(String.format("少女祈祷中, 请稍等 dict-12-v2 %d/%d", i, Lia.DICT_12_PARTS));
		    }
		    conn.setTransactionSuccessful();
		} finally {
			conn.endTransaction();
		}
		conn.close();
    }

    public void upgrade() {
    	File pathOld = new File(Lia.LIA_PATH_OLD);
    	File path = new File(Lia.LIA_PATH);
    	if (pathOld.exists() == true && path.exists() == false)
    		pathOld.renameTo(path);
    }
    
    public void InstallFileExistInSDCard() {
    	try {
    		File path = new File(Lia.LIA_PATH);
        	if (path.exists() == false) {
        		Log.d("lia", "path didn't exist, create folder");
        		path.mkdir();
        	}
        	setupFile(Lia.WL_CET4_PATH, "wl-cet-4");
        	setupFile(Lia.WL_CET6_PATH, "wl-cet-6");
        	setupFile(Lia.WL_KAOYAN_PATH, "wl-kaoyan");
        	setupFile(Lia.WL_IELTS_PATH, "wl-ielts");
        	setupFile(Lia.WL_TOFEL_PATH, "wl-tofel");
        	setupFile(Lia.WL_GRE_PATH, "wl-gre");
        	setupDict();
        	handler.sendEmptyMessage(1);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void sendDisplayMessage(String msgStr) {
		Message m = new Message();
		m.what = 0;
		m.obj = msgStr;
		handler.sendMessage(m);    	
    }
    
	public void run() {
		upgrade();
		if (false == isSDCardMounted()) {
			sendDisplayMessage("SD卡不能读取呢 ... 是不是正在连接电脑中?");
			return ;
		}
		InstallFileExistInSDCard();
	}
	
	private boolean isSDCardMounted() {
		String status = Environment.getExternalStorageState();
		return status.equals(Environment.MEDIA_MOUNTED);
	}
}

public class InitActivity extends Activity {
	private Handler handler;
	private TextView initText;
    

    
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init);
		initText = (TextView)findViewById(R.id.textView1);
		initText.setText("少女祈祷中...");

		
		
		if (isFileAllExists() == false) {
			handler = new Handler() {
			    @Override
			    public void handleMessage(Message msg) {
			        switch(msg.what){
			        case 0:
			        	initText.setText((String)msg.obj);
			            break;
			        case 1:
			    		Intent it = new Intent(InitActivity.this, LiaActivity.class);
			    		startActivity(it);
			    		finish();
			    		break;
			        }
			    }
			};
		    //
		    // Create check thread
		    //
			Thread installThread = new Thread(new CheckUsefulFiles(this, handler));
			installThread.start();
		} else {
    		Intent it = new Intent(InitActivity.this, LiaActivity.class);
    		startActivity(it);
    		finish();
		}

		
    }	
    public static boolean isFileAllExists() {
    	ArrayList<File> al = new ArrayList<File>();
		al.add(new File(Lia.LIA_PATH));
		al.add(new File(Lia.DICT_12_PATH));
		al.add(new File(Lia.WL_CET4_PATH));
		al.add(new File(Lia.WL_CET6_PATH));
		al.add(new File(Lia.WL_IELTS_PATH));
		al.add(new File(Lia.WL_KAOYAN_PATH));
		al.add(new File(Lia.WL_TOFEL_PATH));
		al.add(new File(Lia.WL_GRE_PATH));
		for (File fp : al) {
    	    if (fp.exists() == false || fp.length() < 10 * 1024)
    	    	return false;
		}
		return true;
    }



}
