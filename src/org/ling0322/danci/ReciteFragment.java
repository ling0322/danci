package org.ling0322.danci;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.os.Bundle;
import android.preference.*;
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
    
    private MainActivity mainActivity;
    private Speech mSpeech;
    
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
    
    public void recitingFinish() {
        recite.finish();
        updateDiaplay();
    }
    
    private void showWordDefi(String word, boolean hideDefi) {
        int scale = (int)getResources().getDisplayMetrics().density;
        int paddingPx = 8 * scale;
        
        
        View v = DefinitionView.getDefinitionView(getActivity(), word, hideDefi);
        LinearLayout containerView = (LinearLayout)getActivity().findViewById(R.id.word_defi_view);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
            );
        v.setPadding(paddingPx, 0, paddingPx, 0);
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
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean speech = prefs.getBoolean("auto_speech", false);
        ((LinearLayout)getActivity().findViewById(R.id.word_defi_view)).removeAllViews();
        
        if (recite.isFinsihed()) {
            ((ReviewListFragment)mainActivity.getFragmentAdapter().getItem(2)).refleshList();
            showStartButton();
        } else if (state == FIRST) {
            if (speech == true) {
                mSpeech.speak(recite.pickWord());
            }
            showWordDefi(recite.pickWord(), true);
        } else if (state == SECOND) {

            showWordDefi(recite.pickWord(), false);
        }
        
        TextView tv = (TextView)getActivity().findViewById(R.id.textView1);
        tv.setText(recite.getCntState());
    }
    
    private void onStartButtonClicked() {
        state = FIRST;
        recite.start();
        if (recite.isFinsihed() == true) {
            Dialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AnneDialog))
            .setMessage("今天的单词都背完了哦，明天继续复习吧:)")
            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }                    
            }).create();
            dialog.show(); 
        } else {
            showTestButtons();
            updateDiaplay();
        }
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
        mSpeech = new Speech(getActivity());
        return inflater.inflate(R.layout.recite, container, false);
    }

    @Override 
    public boolean onBackKey() {
        if (recite.isFinsihed() == true) {
            System.exit(0);
        }
        
        Dialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AnneDialog))
            .setMessage("单词还没有背完, 确定要退出吗?")
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
            LinearLayout la = (LinearLayout)getActivity().findViewById(R.id.button_container);
            la.removeAllViews();
            la.addView(initButton);
            return ;
        }
            

        t.setText(recite.getCntState());
        showStartButton();
    }
    
    @Override 
    public boolean onRefresh() {
        recite = new Recite();
        showStartButton();
        updateDiaplay();
        return false;
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
            getActivity().startActivityForResult(it, MainActivity.PREFERENCE_REQUEST_ID);
        }
    }
}
