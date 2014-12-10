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

import android.app.Application;
import android.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jboss.aerogear.agreddit.authentication.RedditAuthenticationModule;
import org.jboss.aerogear.agreddit.gson.ListingTypeAdapter;
import org.jboss.aerogear.agreddit.gson.PagingGsonResponseParser;
import org.jboss.aerogear.agreddit.reddit.Listing;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.impl.pipeline.GsonRequestBuilder;
import org.jboss.aerogear.android.impl.pipeline.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.pipeline.PipeManager;
import org.jboss.aerogear.android.pipeline.paging.PageConfig;

import java.net.MalformedURLException;
import java.net.URL;

public class StoryListApplication extends Application {

	private RedditAuthenticationModule authModule;
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Listing.class, new ListingTypeAdapter()).create();
	@Override
	public void onCreate() {
		super.onCreate();
		URL redditURL;
		try {
			redditURL = new URL(getString(R.string.reddit_base) +"hot.json");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		authModule = new RedditAuthenticationModule(getApplicationContext());

		RestfulPipeConfiguration config = PipeManager.config("listing", RestfulPipeConfiguration.class).withUrl(redditURL);

        config.requestBuilder(new GsonRequestBuilder(GSON));
        config.module(authModule);
        PageConfig pageConfig = new PageConfig();
        pageConfig.setLimitValue(25);
        pageConfig.setMetadataLocation(PageConfig.MetadataLocations.BODY);
        pageConfig.setNextIdentifier("data.after");
        pageConfig.setPreviousIdentifier("data.before");
        pageConfig.setPageParameterExtractor(new PageConsumer());

        config.responseParser(new PagingGsonResponseParser(GSON, pageConfig));

        config.pageConfig(pageConfig).forClass(Listing.class);
		
                
                

		
	}
	
	public boolean isLoggedIn() {
		return authModule.isLoggedIn();
	}

	public Pipe<Listing> getListing(Fragment fragment) {
        LoaderPipe pipe = PipeManager.get("listing", fragment, getApplicationContext());
        ((PagingGsonResponseParser)pipe.getResponseParser()).setPipe(pipe);
        return pipe;
	}

	public void login(String username, String password, Callback<HeaderAndBody> callback) {
		authModule.login(username, password, callback);
	}
	
}
