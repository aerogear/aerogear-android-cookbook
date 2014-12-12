package org.jboss.aerogear.android.cookbook.chucknorrisjokes;

import android.app.Activity;
import android.app.Application;

import org.jboss.aerogear.android.cookbook.chucknorrisjokes.model.Joke;
import org.jboss.aerogear.android.impl.pipeline.GsonResponseParser;
import org.jboss.aerogear.android.impl.pipeline.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.pipeline.PipeManager;
import org.jboss.aerogear.android.pipeline.ResponseParser;

import java.net.MalformedURLException;
import java.net.URL;

public class ChuckNorrisJokesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        createPipe();
    }

    private void createPipe() {
        try {

            ResponseParser responseParser = new GsonResponseParser();
            responseParser.getMarshallingConfig().setDataRoot("value");

            PipeManager.config("chuckNorris", RestfulPipeConfiguration.class)
                    .withUrl(new URL("http://api.icndb.com/jokes/random/"))
                    .responseParser(responseParser)
                    .forClass(Joke.class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public LoaderPipe<Joke> getPipe(Activity activity) {
        LoaderPipe loaderPipe = PipeManager.get("chuckNorris", activity);
        return loaderPipe;
    }

}
