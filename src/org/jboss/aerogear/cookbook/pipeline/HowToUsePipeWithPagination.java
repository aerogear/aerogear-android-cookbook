package org.jboss.aerogear.cookbook.pipeline;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.ReadFilter;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.pipeline.paging.PageConfig;
import org.jboss.aerogear.android.pipeline.paging.PagedList;
import org.jboss.aerogear.cookbook.R;
import org.jboss.aerogear.cookbook.model.Car;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HowToUsePipeWithPagination extends Activity {

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

            pipe.readWithFilter(readFilter, readCallback);

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

    private class ReadCallback extends AbstractActivityCallback<List<Car>> {
        public void onSuccess(List<Car> data) {
            HowToUsePipeWithPagination activity = (HowToUsePipeWithPagination) getActivity();
            activity.setData((PagedList<Car>) data);
        }

        public void onFailure(Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
