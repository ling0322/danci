package org.ling0322.danci;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class ReviewListFragment extends CustomFragment implements OnClickListener, OnItemClickListener {
    
    private WordlistDB wldb;
    private TextView progress;
    private ListView listview;
    private ArrayList<String> wordlist;
    private ArrayList<Integer> remainsList;
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
            
 
        progress = (TextView)getActivity().findViewById(R.id.review_progress);
        listview = (ListView)getActivity().findViewById(R.id.review_wordlist);
        buttonContainer = (LinearLayout)getActivity().findViewById(R.id.rev_btn);
        
        nextButton = (Button)getActivity().findViewById(R.id.rev_button_next);
        nextButton.setOnClickListener(this);
        prevButton = (Button)getActivity().findViewById(R.id.rev_button_prev);
        prevButton.setOnClickListener(this);
        
        wordlist =  new ArrayList<String>();
        remainsList = new ArrayList<Integer>();
        adapter = new WordlistAdapter(wordlist, remainsList, getActivity());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
        currentPage = 0;
        wldb = new WordlistDB();
        if (wldb.isNullDbConn() == true) {
            buttonContainer.setVisibility(View.GONE);
            getActivity().findViewById(R.id.review_remains).setVisibility(View.GONE);
            return ;
        }
        refleshList();
    }
    
    public void refleshList() {
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
            progress.setText(String.format("生词本-共%d个", reviewCount));
        } else {
            buttonContainer.setVisibility(View.VISIBLE);
            progress.setText(String.format("生词本-共%d个-第%d页/共%d页", reviewCount, currentPage + 1, pageMax));
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
        }   
    }
    
    @Override
    public boolean onRefresh() {
        refleshList();
        return true;
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        int wordIndex = position;
        Intent it = new Intent(getActivity(), DefinitionActivity.class);
        it.putExtra("word", wordlist.get(wordIndex));
        getActivity().startActivity(it);
    }
}
