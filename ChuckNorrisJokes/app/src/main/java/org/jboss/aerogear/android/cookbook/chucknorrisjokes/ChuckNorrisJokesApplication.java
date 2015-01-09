package org.jboss.aerogear.android.cookbook.chucknorrisjokes;

import android.app.Activity;
import android.app.Application;

import org.jboss.aerogear.android.cookbook.chucknorrisjokes.model.Joke;
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.ResponseParser;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.rest.gson.GsonResponseParser;

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
        LoaderPipe loaderPipe = PipeManager.getPipe("chuckNorris", activity);
        return loaderPipe;
    }

}
