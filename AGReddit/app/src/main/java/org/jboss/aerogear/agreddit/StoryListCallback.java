package org.jboss.aerogear.agreddit;

import java.util.List;

import org.jboss.aerogear.agreddit.reddit.Listing;
import org.jboss.aerogear.agreddit.reddit.T3;
import org.jboss.aerogear.android.http.HttpException;
import org.jboss.aerogear.android.impl.pipeline.paging.WrappingPagedList;
import org.jboss.aerogear.android.pipeline.AbstractFragmentCallback;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
