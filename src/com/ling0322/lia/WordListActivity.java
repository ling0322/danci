package com.ling0322.lia;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.*;
import android.widget.*;
import android.database.*;
import android.database.sqlite.*;


public class WordListActivity extends Activity implements OnClickListener {
    public ArrayAdapter<String> adapter;
    public ArrayList<String> words;
    public final static int WORDS_PER_PAGE = 100;
    private SQLiteDatabase db;
    private int page = 0;
    private int pagesCount = 0;
    
    private String escape(String raw) {
        return raw.replaceAll("\\$x27", "'").replaceAll("\r\n", "").replaceAll("\\$x0A", "\n");
    }
    
    private void goToPage(int page) {
        int startOffset = page * WORDS_PER_PAGE;
        Cursor c = db.rawQuery(String.format(
            "select word, definition " + 
            "from dict " + 
            "limit %d offset %d ", 
            WORDS_PER_PAGE, startOffset), null);

        words.clear();
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); ++i) {
            words.add(escape(c.getString(0)) + "\n" + escape(c.getString(1).replace('\n', '\0')));
            c.moveToNext();
        }
        c.close();
        EditText t = (EditText)findViewById(R.id.page_edit);
        t.setText(String.valueOf(page + 1));
        adapter.notifyDataSetChanged();
    }
    
    private void nextPage() {
        page = (page + 1) % pagesCount;
        goToPage(page);
    }
    
    private void prevPage() {
        page = (pagesCount + page - 1) % pagesCount;
        goToPage(page);        
    }
    
    public void onClick(View view) {
        Button b = (Button)view;
        switch (b.getId()) {
        case R.id.next_button:
            nextPage();
            break;
        case R.id.prev_button:
            prevPage();
            break;
        case R.id.go_button:
            EditText t = (EditText)findViewById(R.id.page_edit);
            int p = Integer.parseInt(t.getText().toString());
            if (p > 0 && p <= pagesCount) {
                page = p - 1;
                goToPage(page);
            }
            
        }
    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);
        
        //
        // create db interface
        //
        File dbfile = new File("/sdcard/wordlist"); 
        db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        
        //
        // get page count 
        //
        Cursor c = db.rawQuery("select count(*) from dict", null);
        c.moveToFirst();
        pagesCount = c.getInt(0) / WORDS_PER_PAGE + 1;
        c.close();
        TextView tv = (TextView)findViewById(R.id.page_count);
        tv.setText("/" + String.valueOf(pagesCount));
        
        //
        // set listener for each button
        //
        Button nextButton = (Button)findViewById(R.id.next_button);
        Button prevButton = (Button)findViewById(R.id.prev_button);
        Button goButton = (Button)findViewById(R.id.go_button);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        goButton.setOnClickListener(this);
        
        //
        // initialize the list view
        //
        words = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, words);
        ListView lv = (ListView)findViewById(R.id.listView1);
        lv.setAdapter(adapter);
        
        //
        // show the first page 
        //
        goToPage(page);
    }


}
