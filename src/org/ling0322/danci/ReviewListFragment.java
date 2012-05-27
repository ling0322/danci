package org.ling0322.danci;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.*;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class ReviewListFragment extends CustomFragment implements OnClickListener {
    
    private WordlistDB wldb;
    private TextView progress;
    private ListView listview;
    private ArrayList<String> wordlist;
    private WordlistAdapter adapter;
    private LinearLayout buttonContainer;
    private Button prevButton;
    private Button nextButton;
    private int currentPage;
    private int pageMax;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.review_list, container, false);
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            
        wldb = new WordlistDB(Wordlist.getWordlistDbConn());
        if (wldb.isNullDbConn() == true)
            return ;
 
        progress = (TextView)getActivity().findViewById(R.id.review_progress);
        listview = (ListView)getActivity().findViewById(R.id.review_wordlist);
        buttonContainer = (LinearLayout)getActivity().findViewById(R.id.rev_btn);
        nextButton = (Button)getActivity().findViewById(R.id.rev_button_next);
        nextButton.setOnClickListener(this);
        prevButton = (Button)getActivity().findViewById(R.id.rev_button_prev);
        prevButton.setOnClickListener(this);
        wordlist =  new ArrayList<String>();
        adapter = new WordlistAdapter(wordlist, getActivity());
        listview.setAdapter(adapter);
        currentPage = 0;
        
        reflesh();
    }
    
    public void reflesh() {
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
        wordlist.addAll(wldb.reviewListAll(Config.REVIEWLIST_WORDS_PER_PAGE, currentPage));
        Log.d("lia", String.valueOf(wordlist.size()));
        adapter.notifyDataSetChanged();
    }

    public void onClick(View view) {
        if (view == nextButton) {
            currentPage = (currentPage + 1) % pageMax;
        } else if (view == prevButton) {
            currentPage = (currentPage - 1) % pageMax;
        }
        reflesh();
    }
}
