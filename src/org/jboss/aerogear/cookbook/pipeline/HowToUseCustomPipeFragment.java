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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.pipeline.support.AbstractSupportFragmentCallback;
import org.jboss.aerogear.cookbook.CarAdapter;
import org.jboss.aerogear.cookbook.model.Car;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class HowToUseCustomPipeFragment extends ListFragment {

    private Pipe<Car> carsPipe;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        try {
            URL fileURL = getActivity().getFilesDir().toURI().toURL();
            Pipeline pipeline = new Pipeline(fileURL);
            PipeConfig fileReaderConfig = new PipeConfig(fileURL, Car.class);
            Context applicationContext = this.getActivity().getApplicationContext();
            fileReaderConfig.setHandler(new FileHandler(applicationContext));
            pipeline.pipe(Car.class, fileReaderConfig);

            // Retrive Pipe
            carsPipe = (LoaderPipe<Car>) pipeline.get("car", this, applicationContext);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        carsPipe.read(new FileCallback());
    }

    private static final class FileCallback extends
            AbstractSupportFragmentCallback<List<Car>> {

        private static final long serialVersionUID = 1L;

        public FileCallback() {
            super("hashableValue");
        }

        @Override
        public void onSuccess(List<Car> data) {
            ListFragment fragment = (ListFragment) getFragment();
            fragment.setListAdapter(new CarAdapter(fragment.getActivity(), data));
        }

        @Override
        public void onFailure(Exception e) {
            Toast.makeText(getFragment().getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }

}
