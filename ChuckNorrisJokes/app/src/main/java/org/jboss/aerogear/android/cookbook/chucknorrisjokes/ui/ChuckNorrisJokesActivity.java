package org.jboss.aerogear.android.cookbook.chucknorrisjokes.ui;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jboss.aerogear.android.cookbook.chucknorrisjokes.ChuckNorrisJokesApplication;
import org.jboss.aerogear.android.cookbook.chucknorrisjokes.R;
import org.jboss.aerogear.android.cookbook.chucknorrisjokes.model.Joke;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;
import org.jboss.aerogear.android.pipeline.LoaderPipe;

import java.util.List;

public class ChuckNorrisJokesActivity extends ActionBarActivity {

    private ChuckNorrisJokesApplication application;
    private MenuItem mRefreshItem;
    private TextView mJoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chuck_norris_jokes);

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
            startActionBarAnimation(item);
            dismissJoke();
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
                stopActionBarAnimation();
            }

            @Override
            public void onFailure(Exception e) {
                displayError(e);
                stopActionBarAnimation();
            }
        });
    }

    private void startActionBarAnimation(MenuItem item) {
        this.mRefreshItem = item;

        Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView imageView = (ImageView) inflater.inflate(R.layout.image_refresh, null);
        imageView.startAnimation(rotation);

        mRefreshItem.setActionView(imageView);
    }

    private void stopActionBarAnimation() {
        if ((mRefreshItem != null) && (mRefreshItem.getActionView() != null)) {
            mRefreshItem.getActionView().clearAnimation();
            mRefreshItem.setActionView(null);
        }
    }

    private void displayJoke(Joke joke) {
        mJoke.setText(joke.getJoke());
        mJoke.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down_in));
//        mJoke.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_in));
//        mJoke.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_left_to_right));
    }

    private void dismissJoke() {
        mJoke.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down_out));
//        mJoke.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_from_left_to_right));
    }

    private void displayError(Exception e) {
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
