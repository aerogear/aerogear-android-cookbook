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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class StoryListActivity extends Activity
        implements StoryListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_list);

        
        
        if (findViewById(R.id.story_detail_container) != null) {
            mTwoPane = true;
            ((StoryListFragment) getFragmentManager()
                    .findFragmentById(R.id.story_list))
                    .setActivateOnItemClick(true);
            ((StoryListFragment) getFragmentManager()
                    .findFragmentById(R.id.story_list))
                    .setRetainInstance(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (((StoryListApplication)getApplication()).isLoggedIn()) {
        	inflater.inflate(R.menu.logout, menu);
        } else {
        	inflater.inflate(R.menu.login, menu);
        }
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.menu_login:
			LoginDialogFragment dialog = new LoginDialogFragment();
			dialog.show(getFragmentManager(), "Dialog");
			break;
            case R.id.menu_reload:
                ((StoryListFragment) getFragmentManager()
                        .findFragmentById(R.id.story_list)).reload(true);
                break;

		default:
			break;
		}
    	return super.onMenuItemSelected(featureId, item);
    }
    
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(StoryDetailFragment.ARG_ITEM_ID, id);
            StoryDetailFragment fragment = new StoryDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.story_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, StoryDetailActivity.class);
            detailIntent.putExtra(StoryDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
