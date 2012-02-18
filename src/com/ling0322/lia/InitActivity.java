package com.ling0322.lia;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    	if (fp.exists() == false) {
    		OutputStream out = new FileOutputStream(fp);
    		InputStream in = activity.getAssets().open(from);
    		byte [] buf = new byte[8192];
			Message m = new Message();
			m.what = 0;
			m.obj = String.format("少女祈祷中, 请稍等 %s", from);
			handler.sendMessage(m);
    		while (-1 != in.read(buf)) {
    			out.write(buf);
    		}
    		in.close();
    		out.close();
    	}
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
        	setupFile(Lia.DICT_12_PATH, "dict-12.ogg");
        	handler.sendEmptyMessage(1);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
	public void run() {
		InstallFileExistInSDCard();
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
		for (File fp : al) {
    	    if (fp.exists() == false)
    	    	return false;
		}
		return true;
    }



}
