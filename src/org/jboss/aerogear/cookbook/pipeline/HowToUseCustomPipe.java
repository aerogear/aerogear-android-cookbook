/**
 * JBoss, Home of Professional Open Source
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
package org.jboss.aerogear.cookbook.pipeline;

import android.util.Log;
import android.widget.Toast;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.android.pipeline.support.AbstractFragmentActivityCallback;
import org.jboss.aerogear.cookbook.Constants;
import org.jboss.aerogear.cookbook.ProgressFragment;
import org.jboss.aerogear.cookbook.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import org.jboss.aerogear.cookbook.model.Developer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HowToUseCustomPipe extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developers);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new ProgressFragment())
                .commit();

        retriveData();
    }

    private void retriveData() {
        try {
            URL fileURL = this.getFilesDir().toURI().toURL();

            Pipeline pipeline = new Pipeline(fileURL);
            PipeConfig pipeConfig = new PipeConfig(fileURL, Developer.class);

            pipeConfig.setHandler(new FileHandler(getApplicationContext()));
            pipeline.pipe(Developer.class, pipeConfig);

            LoaderPipe<Developer> developerLoaderPipe = pipeline.get("developer", this);
            developerLoaderPipe.read(new TeamReadCallBack());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static class TeamReadCallBack extends AbstractFragmentActivityCallback<List<Developer>> {
        @Override
        public void onSuccess(List<Developer> data) {
            HowToUseCustomPipe activity = (HowToUseCustomPipe) getFragmentActivity();
            activity.displayTeam(data);
        }

        @Override
        public void onFailure(Exception e) {
            HowToUseCustomPipe activity = (HowToUseCustomPipe) getFragmentActivity();
            activity.displayErrorMessage(e);
        }
    }

    private void displayTeam(List<Developer> data) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new DeveloperFragment(this, data))
                .commit();
    }

    private void displayErrorMessage(Exception e) {
        Log.e(Constants.TAG, e.getMessage(), e);
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
