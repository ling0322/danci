package org.ling0322.danci;

import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.preference.*;
import android.util.*;

public class ClearDialog extends DialogPreference  {
    private Context mContext;
    
    public ClearDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    
    private void resetReciting() {
        SQLiteDatabase dbsqlite = Wordlist.openWordlistDb(mContext);
        if (dbsqlite == null)
            return ;
        String sqlCmd = "update dict set tested = 0, correct = 0, continuous_correct = 0, last_tested = 0";
        dbsqlite.execSQL(sqlCmd);
        dbsqlite.close();
        // ReciteActivity.instance.finish();
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.i("lia", "confirm" + String.format("%d", which));
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            resetReciting();
            break;
        default:
            break;
        }
    }
}
