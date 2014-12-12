package org.jboss.aerogear.android.cookbook.chucknorrisjokes;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.cookbook.chucknorrisjokes.model.Joke;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.android.pipeline.Pipe;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    private ChuckNorrisJokesApplication application;
    private TextView mJoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (ChuckNorrisJokesApplication) getApplication();
        mJoke = (TextView) findViewById(R.id.joke);

        retrieveJoke();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            retrieveJoke();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void retrieveJoke() {
        LoaderPipe<Joke> pipe = application.getPipe(this);
        pipe.reset();
        pipe.read(new AbstractActivityCallback<List<Joke>>() {
            @Override
            public void onSuccess(List<Joke> jokes) {
                displayJoke(jokes.get(0));
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void displayJoke(Joke joke) {
        mJoke.setText(joke.getJoke());
    }

    private void displayError(Exception e) {

    }

}
