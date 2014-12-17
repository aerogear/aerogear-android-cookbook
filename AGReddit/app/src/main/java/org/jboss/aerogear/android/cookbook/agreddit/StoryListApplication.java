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
package org.jboss.aerogear.android.cookbook.agreddit;

import android.app.Application;
import android.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jboss.aerogear.android.cookbook.agreddit.authentication.RedditAuthenticationModule;
import org.jboss.aerogear.android.cookbook.agreddit.gson.ListingTypeAdapter;
import org.jboss.aerogear.android.cookbook.agreddit.gson.PagingGsonResponseParser;
import org.jboss.aerogear.android.cookbook.agreddit.reddit.Listing;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.http.HeaderAndBody;
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

        /* The Reddit pagination information is embedded in the body of a Listing response.
        *  This block instructs AeroGear how to extract the information.
        *
        *  see : https://www.reddit.com/dev/api#listings
        *
        */
        PageConfig pageConfig = new PageConfig();

        /* We should limit the number of items in a response to 25*/
        pageConfig.setLimitValue(25);
        /* AeroGear should look in the body of the response for paging metadata. */
        pageConfig.setMetadataLocation(PageConfig.MetadataLocations.BODY);
        /* The metadata for the next data set is the after property of the response data. */
        pageConfig.setNextIdentifier("data.after");
        /* The metadata for the previous data set is the before property of the response data. */
        pageConfig.setPreviousIdentifier("data.before");
        /* PageConsumer is responsible for extracting paging data from the response and providing it
          to the pipe*/
        pageConfig.setPageParameterExtractor(new PageConsumer());



        RestfulPipeConfiguration config = PipeManager.config("listing", RestfulPipeConfiguration.class);
        config.withUrl(redditURL)
                /* Use Reddit's authentication for access.*/
                .module(authModule)
                /* Reddit includes some unique structures in its response that we must configure.*/
                .responseParser(new PagingGsonResponseParser(GSON, pageConfig))
                /* Apply our paging configuration*/
                .pageConfig(pageConfig)
                /* Create the Pipe and register it with PipeManager*/
                .forClass(Listing.class);

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
