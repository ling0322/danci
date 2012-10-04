package org.ling0322.danci;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class DefinitionActivity extends Activity implements OnClickListener {
    private Button mBackButton;
    private LinearLayout mDefinitionViewContainer;
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.definition);
		
		mBackButton = (Button)findViewById(R.id.back_button);
		mBackButton.setOnClickListener(this);
		
		Intent it = this.getIntent();
		String word = it.getStringExtra("word");
		View definitionView = DefinitionView.getDefinitionView(this, word);
		int scale = (int)getResources().getDisplayMetrics().density;
		int paddingPx = 5 * scale;
		
		definitionView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
		mDefinitionViewContainer = (LinearLayout)findViewById(R.id.definition_view);
		mDefinitionViewContainer.addView(definitionView, new LinearLayout.LayoutParams(
		    LinearLayout.LayoutParams.MATCH_PARENT,
		    LinearLayout.LayoutParams.MATCH_PARENT));
		
    }
    public void onClick(View arg0) {
        finish();
    }
}
