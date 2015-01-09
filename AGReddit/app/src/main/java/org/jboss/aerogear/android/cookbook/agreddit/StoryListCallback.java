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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jboss.aerogear.android.cookbook.agreddit.reddit.Listing;
import org.jboss.aerogear.android.cookbook.agreddit.reddit.T3;
import org.jboss.aerogear.android.pipe.callback.AbstractFragmentCallback;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.paging.WrappingPagedList;

import java.util.List;

public class StoryListCallback<T> extends
        AbstractFragmentCallback<List<Listing>> {

	public StoryListCallback(StoryListFragment f) {
		super(f, 1);
	}

	public void onSuccess(final List<Listing> data) {
		final StoryListFragment f = (StoryListFragment) getFragment();
		Log.d("Reddt", "success");
		Log.d("Reddit", data.toString());
		f.listings = (WrappingPagedList<Listing>) data;
		f.setListAdapter(new ArrayAdapter<T3>(f.getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, data.get(0).getData().getChildren()) {

			@Override
			public View getView(int position, View convertView,
					android.view.ViewGroup parent) {
				T3 story = data.get(0).getData().getChildren().get(position);
				if (convertView == null) {
					convertView = ((LayoutInflater) f.getActivity()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
							.inflate(R.layout.reddit_story_list_item, null);
				}

				String author = story.getAuthor();
				String title = story.getTitle();
				Long ups = story.getScore();
				String subreddit = story.getSubreddit();

				((TextView) convertView.findViewById(R.id.title))
						.setText(title);
				((TextView) convertView.findViewById(R.id.author))
						.setText(author);
				((TextView) convertView.findViewById(R.id.upvotes)).setText(ups
						+ "");
				((TextView) convertView.findViewById(R.id.subreddit))
						.setText(subreddit);

				return convertView;
			}
		});
	}

	public void onFailure(Exception e) {
		Log.d("Reddt", "failure", e);
		if (e instanceof HttpException) {
			HttpException httpException = (HttpException) e;
			Log.d("Reddit", new String(httpException.getData()));
		}
	}

}
