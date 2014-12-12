/**
 * JBoss,Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
