package org.jboss.aerogear.guides;

import android.app.Activity;
import android.app.Application;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.guides.pipes.custom.Car;
import org.jboss.aerogear.guides.pipes.custom.FileHandler;

import java.net.MalformedURLException;
import java.net.URL;

public class GuideApplication extends Application {

    private Pipeline pipelineForCustomPipeGuide;

    @Override
    public void onCreate() {
        super.onCreate();
        URL fileURL;

        try {
            fileURL = getFilesDir().toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        pipelineForCustomPipeGuide = new Pipeline(fileURL);
        PipeConfig fileReaderConfig = new PipeConfig(fileURL, Car.class);
        fileReaderConfig.setHandler(new FileHandler(this));
        pipelineForCustomPipeGuide.pipe(Car.class, fileReaderConfig);
    }

    public LoaderPipe<Car> getCarPipe(Activity activity) {
        return (LoaderPipe<Car>) pipelineForCustomPipeGuide.get("car", activity);
    }

}
