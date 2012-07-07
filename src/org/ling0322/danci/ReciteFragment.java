package org.ling0322.danci;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.*;


public class ReciteFragment extends BaseFragment implements OnClickListener {
    /** Called when the activity is first created. */
    
    private Recite mRecite;
    
    private Button mStartButton;
    private Button mYesButton;
    private Button mNoButton;
    private Button mNextButton;
    private Button mInitButton;
    private LinearLayout mButtonContainer;
    private TextView mStateMessage;
    private ReviewListFragment mReviewListFragment;
    private LinearLayout mDefinitionContainer;
    private RecitingMachine mRecitingMachine;
    private Speech mSpeech;
    private SharedPreferences mPreferences;
    

    private class IState {
        public void onStartButton() {}
        public void onYesButton() {}
        public void onNoButton() {}
        public void onNextButton() {}
        public void onStateSwitchedTo() {}
    }
    
    private class StateEnd extends IState {
        private RecitingMachine mReciteMachine;
        
        public StateEnd(RecitingMachine reciteMachine) {
            mReciteMachine = reciteMachine;
        }
        
        public void onStateSwitchedTo() {
            mDefinitionContainer.removeAllViews();
            
            mRecite = new Recite(getActivity());
            
            if (mRecite.isNullDbConn() == true) {
                mStateMessage.setText("单词喵喵喵: 请先点击下方按钮, 选择一个的单词表");
                showInitButton();
                return ;
            }
 
            mStateMessage.setText(mRecite.getCntState());
            
            // refresh review list to apply current changes
            
            mReviewListFragment.refleshList();
            
            showStartButton();
        }
        
        public void onStartButton() {
            mRecite.start();
            mReciteMachine.setState(mReciteMachine.getFirstState());
        }
    }
    
    private class StateFirst extends IState {
        private RecitingMachine mReciteMachine;
        
        public StateFirst(RecitingMachine reciteMachine) {
            mReciteMachine = reciteMachine;
        }
        
        public void onStateSwitchedTo() {

            mDefinitionContainer.removeAllViews();
            boolean isAutoSpeak = mPreferences.getBoolean("auto_speech", false);
            if (isAutoSpeak == true)
                mSpeech.speak(mRecite.pickWord());
            showWordDefi(mRecite.pickWord(), true);
            showTestButtons();
            mStateMessage.setText(mRecite.getCntState());
        }

        public void onYesButton() {
            mReciteMachine.setState(mReciteMachine.getSecondState());
            showTest2Buttons();
        }

        public void onNoButton() {
            mReciteMachine.setState(mReciteMachine.getSecondState());
            showNextButton();
        }
    }
    
    private class StateSecond extends IState {
        
        private RecitingMachine mReciteMachine;
        
        public StateSecond(RecitingMachine reciteMachine) {
            mReciteMachine = reciteMachine;
        }
        
        public void onStateSwitchedTo() {
            mDefinitionContainer.removeAllViews();
            showTest2Buttons();
            showWordDefi(mRecite.pickWord(), false);
        }
        
        public void onYesButton() {
            mRecite.answer(true);
            if (mRecite.isFinsihed()) 
                mReciteMachine.setState(mReciteMachine.getEndSteate());
            else
                mReciteMachine.setState(mReciteMachine.getFirstState());
        }

        public void onNoButton() {
            mRecite.answer(false);
            mReciteMachine.setState(mReciteMachine.getFirstState());
            
            // NoButton click event wouldn't switch state to end
        }

        public void onNextButton() {
            mRecite.answer(false);
            if (mRecite.isFinsihed()) 
                mReciteMachine.setState(mReciteMachine.getEndSteate());
            else
                mReciteMachine.setState(mReciteMachine.getFirstState());
        }
        
    }
    
    private class RecitingMachine {
        private IState mCurrentState;
        private IState mFirstState;
        private IState mSecondState;
        private IState mEndState;
        
        public RecitingMachine() {
            mFirstState = new StateFirst(this);
            mSecondState = new StateSecond(this);
            mEndState = new StateEnd(this);
        }
        
        public IState getFirstState() {
            return mFirstState;
        }
        
        public IState getSecondState() {
            return mSecondState;
        }

        public IState getEndSteate() {
            return mEndState;
        }
        
        public void setState(IState state) {
            mCurrentState = state;
            mCurrentState.onStateSwitchedTo();
        }

        public void onStartButton() {
            mCurrentState.onStartButton();
        }

        public void onYesButton() {
            mCurrentState.onYesButton();
        }

        public void onNoButton() {
            mCurrentState.onNoButton();
        }

        public void onNextButton() {
            mCurrentState.onNextButton();
        }
        
    }
    

    
    public ReciteFragment() {
    }
    
    private void showStartButton() {
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(mStartButton);
    }
    
    private void showInitButton() {
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(mInitButton);
    }    
    
    private void showTestButtons() {
        mButtonContainer.removeAllViews();
        mNoButton.setText("不记得了 TwT");
        mYesButton.setText("我知道 :)");
        mButtonContainer.addView(mNoButton);
        mButtonContainer.addView(mYesButton);
    }
    
    private void showTest2Buttons() {
        mButtonContainer.removeAllViews();
        mNoButton.setText("记错了 QAQ");
        mYesButton.setText("正确 =w=");
        mButtonContainer.addView(mNoButton);
        mButtonContainer.addView(mYesButton);
    }
    
    private void showNextButton() {
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(mNextButton);        
    }
    
    private void showWordDefi(String word, boolean hideDefi) {
        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        View definitionView = DefinitionView.getDefinitionView(getActivity(), word, hideDefi);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            LinearLayout.LayoutParams.FILL_PARENT
            );
        lp.setMargins(screenWidth / 40, 0, screenWidth / 40, 0);
        definitionView.setLayoutParams(lp);
        mDefinitionContainer.removeAllViews();
        mDefinitionContainer.addView(definitionView);    
    }

    @Override
    public void onPageSelected() {
    	MainActivity mainActivity = (MainActivity)getActivity();
    	if (mainActivity != null)
    	    mainActivity.closeIME();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity)getActivity();
        if (mainActivity != null)
            mainActivity.closeIME();
    }
    
    
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recite, container, false);

    }

    @Override 
    public boolean onBackKey() {
        System.exit(0);
        return true;

    }

    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
     
        mRecite = new Recite(getActivity());
        MainActivity mainActivity = (MainActivity)getActivity();
        
        mButtonContainer = (LinearLayout)getActivity().findViewById(R.id.button_container);
        mStartButton = (Button)getActivity().findViewById(R.id.start_button);
        mYesButton = (Button)getActivity().findViewById(R.id.yes_button);
        mNoButton = (Button)getActivity().findViewById(R.id.no_button);
        mNextButton = (Button)getActivity().findViewById(R.id.next_button);
        mInitButton = (Button)getActivity().findViewById(R.id.init_button);
        mDefinitionContainer = (LinearLayout)getActivity().findViewById(R.id.word_defi_view);
        
        mStartButton.setOnClickListener(this);
        mYesButton.setOnClickListener(this);
        mNoButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mInitButton.setOnClickListener(this);
        
        mStateMessage = (TextView)getActivity().findViewById(R.id.textView1);
        mReviewListFragment = ((ReviewListFragment)mainActivity.getFragmentAdapter().getItem(2));   
        
        mRecitingMachine = new RecitingMachine();
        mRecitingMachine.setState(mRecitingMachine.getEndSteate());
        mSpeech = new Speech(getActivity());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }
    
    public void onClick(View arg0) {
        switch(arg0.getId()) {
        case R.id.start_button:
            mRecitingMachine.onStartButton();
            break;
        case R.id.no_button:
            mRecitingMachine.onNoButton();
            break;
        case R.id.yes_button:
            mRecitingMachine.onYesButton();
            break;
        case R.id.next_button:
            mRecitingMachine.onNextButton();
            break;
        case R.id.init_button:
            Intent it = new Intent(getActivity(), LiaPreferencesActivity.class);
            getActivity().startActivityForResult(it, MainActivity.PREFERENCE_REQUEST_ID);
        }
    }
    
    @Override 
    public boolean onRefresh() {
        mRecitingMachine.setState(mRecitingMachine.getEndSteate());
        return true;
    }
}
