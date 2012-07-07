package org.ling0322.danci;
import android.app.Activity;
import android.support.v4.app.*;

public class BaseFragment extends Fragment {
    public boolean onBackKey() {
    	return false;
    }
    
    public void onPageSelected() {
    }
    
    public boolean onRefresh() {
        return false;
    }
}
