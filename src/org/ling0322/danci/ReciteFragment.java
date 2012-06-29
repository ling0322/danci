package org.ling0322.danci;

import java.io.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.*;
import android.os.Bundle;
import android.preference.*;
import android.support.v4.app.*;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.*;


public class ReciteFragment extends CustomFragment implements OnClickListener {
    /** Called when the activity is first created. */
    
    private Recite recite;
    
    private Button startButton;
    private Button yesButton;
    private Button noButton;
    private Button nextButton;
    private Button initButton;
    
    private MediaPlayer mp;
    private SharedPreferences prefs;
    private MainActivity mainActivity;
    
    public ReciteFragment() {
    	
    }
    
    private void showStartButton() {
        LinearLayout la = (LinearLayout)getActivity().findViewById(R.id.button_container);
        la.removeAllViews();
        la.addView(startButton);
    }
    
    private void showTestButtons() {
        LinearLayout la = (LinearLayout)getActivity().findViewById(R.id.button_container);
        la.removeAllViews();
        noButton.setText("不记得了 TwT");
        yesButton.setText("我知道 :)");
        la.addView(noButton);
        la.addView(yesButton);
    }
    
    private void showTest2Buttons() {
        LinearLayout la = (LinearLayout)getActivity().findViewById(R.id.button_container);
        la.removeAllViews();
        noButton.setText("记错了 QAQ");
        yesButton.setText("正确 =w=");
        la.addView(noButton);
        la.addView(yesButton);
    }
    
    private void showNextButton() {
        LinearLayout la = (LinearLayout)getActivity().findViewById(R.id.button_container);
        la.removeAllViews();
        la.addView(nextButton);        
    }
    
    private final int FIRST = 0;
    private final int SECOND = 1;    
    private int state = FIRST;
    
    private void speech(String word) {
        File speechFile = new File(String.format("%s/%c/%s.mp3", Config.SPEECH_PATH, word.charAt(0), word));
        if (speechFile.exists() == false)
            return ;
        
        try {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.reset();
            mp.setVolume(1, 1);
            mp.setDataSource(speechFile.getAbsolutePath());
            mp.prepare();
            mp.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void recitingFinish() {
        recite.finish();
        updateDiaplay();
    }
    
    private void showWordDefi(String word, boolean hideDefi) {
        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        View v = DefinitionView.getDefinitionView(getActivity(), word, hideDefi);
        LinearLayout containerView = (LinearLayout)getActivity().findViewById(R.id.word_defi_view);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            LinearLayout.LayoutParams.FILL_PARENT
            );
        lp.setMargins(screenWidth / 40, 0, screenWidth / 40, 0);
        v.setLayoutParams(lp);
        containerView.removeAllViews();
        containerView.addView(v);    
    }

    @Override
    public void onPageSelected() {
    	if (mainActivity != null)
    	    mainActivity.closeIME();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mainActivity != null)
            mainActivity.closeIME();
    }
    
    private void updateDiaplay() {
        if (true == recite.isNullDbConn())
            return ;
        String reciting_mode = prefs.getString("reciting_mode", "word_to_definition");
        Log.i("lia", reciting_mode);
        boolean speech = prefs.getBoolean("auto_speech", false);
        ((LinearLayout)getActivity().findViewById(R.id.word_defi_view)).removeAllViews();
        if (recite.isFinsihed()) {
            ((ReviewListFragment)mainActivity.getFragmentAdapter().getItem(2)).refleshList();
            showStartButton();
            TextView t = (TextView)getActivity().findViewById(R.id.textView1);
            t.setText(recite.getCntState());
            return ;
        }
        if (state == FIRST) {
            if (reciting_mode.equals("word_to_definition")) {
                if (speech == true) {
                    speech(recite.pickWord());
                }
                showWordDefi(recite.pickWord(), true);
            } else {
                showWordDefi(recite.pickWord(), true);       
            }

        } else if (state == SECOND) {
            if (reciting_mode.equals("definition_to_word")) {
                speech(recite.pickWord());
            }
            showWordDefi(recite.pickWord(), false);
        }
        TextView tv = (TextView)getActivity().findViewById(R.id.textView1);
        tv.setText(recite.getCntState());
    }
    
    private void onStartButtonClicked() {
        state = FIRST;
        recite.start();
        showTestButtons();
        updateDiaplay();
    }
    
    private void onYesButtonClicked() {
        if (state == FIRST) {
            state = SECOND;
            showTest2Buttons();
            updateDiaplay();
        } else if (state == SECOND) {
            recite.answer(true);
            state = FIRST;
            showTestButtons();
            updateDiaplay();
        }
    }
    
    private void onNoButtonClicked() {
        if (state == FIRST) {
            showNextButton();
            state = SECOND;
            updateDiaplay();
        } else if (state == SECOND) {
            recite.answer(false);
            state = FIRST;
            showTestButtons();
            updateDiaplay();
        }
    }
    
    private void onNextButtonClicked() {
        recite.answer(false);
        showTestButtons();
        state = FIRST;
        updateDiaplay();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recite, container, false);

    }

    @Override 
    public boolean onBackKey() {
        Dialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AnneDialog))
            .setMessage("确定要退出吗?")
            .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    System.exit(0);
                }    
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }                    
            }).create();
        dialog.show(); 
        return true;
    }
    public void onDetach() {
        super.onDetach();

    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //
        // pron sqlite file
        //
        TextView t = (TextView)getActivity().findViewById(R.id.textView1);        
        recite = new Recite();
        mainActivity = (MainActivity)getActivity();
        
        startButton = (Button)getActivity().findViewById(R.id.start_button);
        yesButton = (Button)getActivity().findViewById(R.id.yes_button);
        noButton = (Button)getActivity().findViewById(R.id.no_button);
        nextButton = (Button)getActivity().findViewById(R.id.next_button);
        initButton = (Button)getActivity().findViewById(R.id.init_button);
        
        startButton.setOnClickListener(this);
        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        initButton.setOnClickListener(this);
        
        if (recite.isNullDbConn() == true) {
            t.setText("单词喵喵喵: 请先选择一个的单词表");
            startButton.setVisibility(View.GONE);
            yesButton.setVisibility(View.GONE);
            noButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            return ;
        }
            

        t.setText(recite.getCntState());
        //
        // the player to play speech audio
        //
        mp = new MediaPlayer();
        //
        // get some preference data
        //
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        


        showStartButton();
        
        
    }
    
    public void onClick(View arg0) {
        switch(arg0.getId()) {
        case R.id.start_button:
            onStartButtonClicked();
            break;
        case R.id.no_button:
            onNoButtonClicked();
            break;
        case R.id.yes_button:
            onYesButtonClicked();
            break;
        case R.id.next_button:
            onNextButtonClicked();
            break;
        case R.id.init_button:
            Intent it = new Intent(getActivity(), LiaPreferencesActivity.class);
            startActivity(it);
        }
    }
}
