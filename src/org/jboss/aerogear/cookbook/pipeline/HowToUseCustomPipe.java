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

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Toast;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.cookbook.CarAdapter;
import org.jboss.aerogear.cookbook.R;
import org.jboss.aerogear.cookbook.model.Car;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HowToUseCustomPipe extends ListActivity {

    private Pipe<Car> carsPipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_list);

        // Create/Config Pipeline
        try {
            URL fileURL = getFilesDir().toURI().toURL();
            Pipeline pipeline = new Pipeline(fileURL);
            PipeConfig fileReaderConfig = new PipeConfig(fileURL, Car.class);
            fileReaderConfig.setHandler(new FileHandler(this));
            pipeline.pipe(Car.class, fileReaderConfig);

            // Retrive Pipe
            carsPipe = (LoaderPipe<Car>) pipeline.get("car", this);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        carsPipe.read(new FileCallback());
    }

    private static final class FileCallback extends AbstractActivityCallback<List<Car>> {

        private static final long serialVersionUID = 1L;

        public FileCallback() {
            super("hashableValue");
        }

        @Override
        public void onSuccess(List<Car> data) {
            ListActivity activity = (ListActivity) getActivity();
            activity.setListAdapter(new CarAdapter(activity, data));
        }

        @Override
        public void onFailure(Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}
