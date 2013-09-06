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
import org.jboss.aerogear.android.ReadFilter;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.android.pipeline.paging.PageConfig;
import org.jboss.aerogear.android.pipeline.paging.PagedList;
import org.jboss.aerogear.android.pipeline.support.AbstractFragmentActivityCallback;
import org.jboss.aerogear.cookbook.R;
import org.jboss.aerogear.cookbook.model.Car;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HowToUsePipeWithPagination extends FragmentActivity {

    private PagedList<Car> data;
    private ReadCallback readCallback = new ReadCallback();

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_list_with_pagination);

        listView = (ListView) findViewById(R.id.cars);

        try {

            URL BASE_URL = new URL("http://controller-aerogear.rhcloud.com/aerogear-controller-demo/");

            //--- Paging Settings
            PageConfig pageConfig = new PageConfig();
            pageConfig.setLimitValue(5);
            pageConfig.setMetadataLocation(PageConfig.MetadataLocations.WEB_LINKING);

            //--- Pipe Settings
            PipeConfig config = new PipeConfig(BASE_URL, Car.class);
            config.setEndpoint("cars");
            config.setPageConfig(pageConfig);

            Pipeline pipeline = new Pipeline(BASE_URL);
            pipeline.pipe(Car.class, config);

            // It' necessary to getActivity on CallBack
            LoaderPipe<Car> pipe = pipeline.get("car", this);

            //--- Reader Settings
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("color", "red");
            ReadFilter readFilter = new ReadFilter();
            readFilter.setWhere(jsonObject);
            readFilter.setLimit(2);

            pipe.read(readFilter, readCallback);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setData(PagedList<Car> data) {
        this.data = data;
        ArrayAdapter<Car> adapter = new ArrayAdapter<Car>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
    }

    public void nextPressed(View button) {
        data.next(readCallback);
    }

    public void previousPressed(View button) {
        data.previous(readCallback);
    }

    private class ReadCallback extends AbstractFragmentActivityCallback<List<Car>> {
        
		private static final long serialVersionUID = 1L;

		public ReadCallback() {
			super(serialVersionUID);
		}
		
		public void onSuccess(List<Car> data) {
            HowToUsePipeWithPagination activity = (HowToUsePipeWithPagination) getFragmentActivity();
            activity.setData((PagedList<Car>) data);
        }

        public void onFailure(Exception e) {
            Toast.makeText(getFragmentActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
