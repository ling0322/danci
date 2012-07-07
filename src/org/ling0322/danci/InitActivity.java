package org.ling0322.danci;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
    	File f = new File(Config.DICT_12_PATH);
    	if (f.exists() == true && f.length() > 1 * 1024 * 1024)
    		return ;
    	
    	if (f.exists() == true && f.length() < 1 * 1024 * 1024)
    		f.delete();

    	
    	SQLiteDatabase conn = SQLiteDatabase.openOrCreateDatabase(Config.DICT_12_PATH, null);
		conn.beginTransaction();
		try {
			conn.execSQL("create table dict(word text primary key, definition text)");
		    for (int i = 0; i < Config.DICT_12_PARTS; ++i) {
			    BufferedReader in = new BufferedReader(new InputStreamReader(activity.getAssets().open("dict-12-v2.part".concat(Integer.toString(i)))));
		        String line;
			    while (null != (line = in.readLine())) {
		        	String[] sp = line.split(" :: ");
		        	conn.execSQL(
		        		"insert into dict(word, definition) values(?, ?)",
		        		sp);
		        }
			    sendDisplayMessage(String.format("少女祈祷中, 请稍等 dict-12-v2 %d/%d", i, Config.DICT_12_PARTS));
		    }
		    conn.setTransactionSuccessful();
		} finally {
			conn.endTransaction();
		}
		conn.close();
    }

    public void upgrade() {
    	File pathOld = new File(Config.LIA_PATH_OLD);
    	File path = new File(Config.LIA_PATH);
    	if (pathOld.exists() == true && path.exists() == false)
    		pathOld.renameTo(path);
    }
    
    public void InstallFileExistInSDCard() {
    	try {
    		File path = new File(Config.LIA_PATH);
        	if (path.exists() == false) {
        		Log.d("lia", "path didn't exist, create folder");
        		path.mkdir();
        	}
        	setupFile(Config.WL_CET4_PATH, "wl-cet-4");
        	setupFile(Config.WL_CET6_PATH, "wl-cet-6");
        	setupFile(Config.WL_KAOYAN_PATH, "wl-kaoyan");
        	setupFile(Config.WL_IELTS_PATH, "wl-ielts");
        	setupFile(Config.WL_TOFEL_PATH, "wl-tofel");
        	setupFile(Config.WL_GRE_PATH, "wl-gre");
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

public class InitActivity extends Activity implements OnClickListener {
	private Handler handler;
	private TextView initText;
    private final int PREFERENCE_REQUEST_ID = 234;

    
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init);
		initText = (TextView)findViewById(R.id.textView1);
        initText.setText("少女祈祷中...");
        
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                case 0:
                    initText.setText((String)msg.obj);
                    break;
                case 1:
                    if (isFileAllExists() == false) {
                        initText.setText("安装失败, 少女哭泣中 TwT");
                    } else {
                        Intent it = new Intent(InitActivity.this, MainActivity.class);
                        startActivity(it);
                        finish();
                    }
                    break;
                }
            }
        };
        //
        // Create check thread
        //
        Thread installThread = new Thread(new CheckUsefulFiles(this, handler));
        installThread.start();	
    }
    
    public static boolean isFileAllExists() {
    	ArrayList<File> al = new ArrayList<File>();
		al.add(new File(Config.LIA_PATH));
		al.add(new File(Config.DICT_12_PATH));
		al.add(new File(Config.WL_CET4_PATH));
		al.add(new File(Config.WL_CET6_PATH));
		al.add(new File(Config.WL_IELTS_PATH));
		al.add(new File(Config.WL_KAOYAN_PATH));
		al.add(new File(Config.WL_TOFEL_PATH));
		al.add(new File(Config.WL_GRE_PATH));
		for (File fp : al) {
    	    if (fp.exists() == false)
    	    	return false;
    	    if (fp.isFile() && fp.length() < 10 * 1024)
    	        return false;
		}
		return true;
    }

    public void onClick(View arg0) {
        Intent it = new Intent(this, LiaPreferencesActivity.class);
        startActivityForResult(it, PREFERENCE_REQUEST_ID);
    }



}
