package org.jboss.aerogear.agreddit;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;

public class StoryDetailFragment extends WebViewFragment {

    public static final String ARG_ITEM_ID = "item_id";

    String mItem;

    public StoryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Override
    public void onStart() {
    	super.onStart();
    	getWebView().setWebViewClient(new WebViewClient() {
    	    public boolean shouldOverrideUrlLoading(WebView view, String url){
    	        // do your handling codes here, which url is the requested url
    	        // probably you need to open that url rather than redirect:
    	        view.loadUrl(url);
    	        return false; // then it is not handled by default action
    	   }
    	});
    	super.getWebView().loadUrl(mItem);
    }
    
    
    
    
}
