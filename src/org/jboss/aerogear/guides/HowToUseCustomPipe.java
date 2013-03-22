package org.jboss.aerogear.guides;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.guides.pipes.custom.Car;

import java.util.List;

public class HowToUseCustomPipe extends ListActivity {

    Pipe<Car> carsPipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_pipe);
        carsPipe = ((GuideApplication) getApplication()).getCarPipe(this);
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
            activity.setListAdapter(new ArrayAdapter(activity, android.R.layout.simple_list_item_1, data));
        }

        @Override
        public void onFailure(Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}
