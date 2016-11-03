package org.jboss.aerogear.android.cookbook.chucknorrisjokes.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

public class ChuckNorrisJokesActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private ChuckNorrisJokesApplication application;
    private SwipeRefreshLayout mSwipeLayout;
    private TextView mJoke;

    private MenuItem mRefreshItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chuck_norris_jokes);

        application = (ChuckNorrisJokesApplication) getApplication();
        mJoke = (TextView) findViewById(R.id.joke);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);

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
            refresh(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh(MenuItem item) {
        startActionBarAnimation(item);
        refresh();
    }

    private void refresh() {
        dismissJoke();
        retrieveJoke();
    }

    private void retrieveJoke() {
        LoaderPipe<Joke> pipe = application.getPipe(this);
        pipe.reset();
        pipe.read(new AbstractActivityCallback<List<Joke>>() {
            @Override
            public void onSuccess(List<Joke> jokes) {
                displayJoke(jokes.get(0));
                stopActionBarAnimation();
                stopSwipeAnimation();
            }

            @Override
            public void onFailure(Exception e) {
                displayError(e);
                stopActionBarAnimation();
                stopSwipeAnimation();
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

    private void stopSwipeAnimation() {
        mSwipeLayout.setRefreshing(false);
    }

    private void displayJoke(Joke joke) {
        mJoke.setText(joke.getJoke());

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down_in);
        mJoke.startAnimation(animation);
    }

    private void dismissJoke() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_down_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mJoke.setText("");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mJoke.startAnimation(animation);
    }

    private void displayError(Exception e) {
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
