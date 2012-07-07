package org.ling0322.danci;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.*;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class ReviewListFragment extends BaseFragment implements OnClickListener, OnItemClickListener {
    
    private WordlistModel wldb;
    private TextView progress;
    private TextView wordsMessage;
    private ListView listview;
    private ArrayList<String> wordlist;
    private ArrayList<Integer> remainsList;
    private WordlistAdapter adapter;
    private LinearLayout buttonContainer;
    private LinearLayout wordsButtonContainer;
    private Button prevButton;
    private Button nextButton;
    private Button nextWordButton;
    private Button prevWordButton;
    private Button backButton;
    private LinearLayout revDefiContainer;
    private LinearLayout reviewWordlist;
    private int currentState;
    private static final int STATE_LIST = 0;
    private static final int STATE_WORDS = 1;
    private int currentPage;
    private int pageMax;
    private int currentWordIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.review_list, container, false);
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            
 
        progress = (TextView)getActivity().findViewById(R.id.review_progress);
        wordsMessage = (TextView)getActivity().findViewById(R.id.reviewWordsMessage);
        listview = (ListView)getActivity().findViewById(R.id.review_wordlist);
        revDefiContainer = (LinearLayout)getActivity().findViewById(R.id.revDefiContainer);
        
        buttonContainer = (LinearLayout)getActivity().findViewById(R.id.rev_btn);
        wordsButtonContainer = (LinearLayout)getActivity().findViewById(R.id.revWordsButtons);
        
        nextButton = (Button)getActivity().findViewById(R.id.rev_button_next);
        nextButton.setOnClickListener(this);
        prevButton = (Button)getActivity().findViewById(R.id.rev_button_prev);
        prevButton.setOnClickListener(this);
        backButton = (Button)getActivity().findViewById(R.id.revButtonBack);
        backButton.setOnClickListener(this);
        nextWordButton = (Button)getActivity().findViewById(R.id.revButtonNextWord);
        nextWordButton.setOnClickListener(this);
        prevWordButton = (Button)getActivity().findViewById(R.id.revButtonPrevWord);
        prevWordButton.setOnClickListener(this);
        
        wordlist =  new ArrayList<String>();
        remainsList = new ArrayList<Integer>();
        adapter = new WordlistAdapter(wordlist, remainsList, getActivity());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
        currentPage = 0;
        wldb = new WordlistModel(getActivity());
        if (wldb.isNullDbConn() == true) {
            buttonContainer.setVisibility(View.GONE);
            return ;
        }
        refleshList();
    }
    
    private void showWordsView() {
        listview.setVisibility(View.GONE);
        wordsMessage.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        revDefiContainer.setVisibility(View.VISIBLE);
        wordsButtonContainer.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.GONE);
        currentState = STATE_WORDS;
    }

    private void showListView() {
        listview.setVisibility(View.VISIBLE);
        wordsMessage.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        revDefiContainer.setVisibility(View.GONE);
        wordsButtonContainer.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.VISIBLE);
        currentState = STATE_LIST;
    }
    
    private void displayWord(int wordIndex) {
        int reviewCount = wldb.reviewCountAll();
        wordIndex = (wordIndex + reviewCount) % reviewCount;
        currentWordIndex = wordIndex;
        wordsMessage.setText(String.format("生词本: %d/%d", currentWordIndex + 1, reviewCount));
        String word = wldb.reviewListAll(1, wordIndex).first.get(0);
        revDefiContainer.removeAllViews();
        View view = DefinitionView.getDefinitionView(getActivity(), word);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        lp.setMargins(screenWidth / 40, 0, screenWidth / 40, 0);
        view.setLayoutParams(lp);
        revDefiContainer.addView(view);
    }
    
    public void refleshList() {
        if (progress == null)
            return ;
        if (wldb == null) {
            progress.setText("生词本为空");
            buttonContainer.setVisibility(View.GONE);
            return ;
        }
        int reviewCount = wldb.reviewCountAll();
        pageMax = Math.max(0, (reviewCount - 1) / Config.REVIEWLIST_WORDS_PER_PAGE) + 1;
        currentPage = Math.min(currentPage, pageMax - 1);
        if (pageMax == 1) {
            buttonContainer.setVisibility(View.GONE);
            progress.setText(String.format("生词本→合计: %d个单词", reviewCount));
        } else {
            buttonContainer.setVisibility(View.VISIBLE);
            progress.setText(String.format("生词本→合计: %d个单词 - 第%d页/共%d页", reviewCount, currentPage + 1, pageMax));
        }
        wordlist.clear();
        remainsList.clear();
        Pair<ArrayList<String>, ArrayList<Integer>> pair = wldb.reviewListAll(
                Config.REVIEWLIST_WORDS_PER_PAGE, currentPage);
        wordlist.addAll(pair.first);
        
        remainsList.addAll(pair.second);
        Log.d("lia", String.valueOf(wordlist.size()));
        adapter.notifyDataSetChanged();
    }
    

    public void onClick(View view) {
        if (view == nextButton) {
            currentPage = (currentPage + pageMax + 1) % pageMax;
            refleshList();
        } else if (view == prevButton) {
            currentPage = (currentPage + pageMax - 1) % pageMax;
            refleshList();
        } else if (view == backButton) {
            showListView();
            refleshList();
        } else if (view == nextWordButton) {
            displayWord(currentWordIndex + 1);
        } else if (view == prevWordButton) {
            displayWord(currentWordIndex - 1);
        }   
    }
    
    @Override
    public boolean onRefresh() {
        showListView();
        refleshList();
        return true;
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        int wordIndex = position + currentPage * Config.REVIEWLIST_WORDS_PER_PAGE;
        showWordsView();
        displayWord(wordIndex);
    }
}
