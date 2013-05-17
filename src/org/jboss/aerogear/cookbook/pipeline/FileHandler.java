package org.jboss.aerogear.cookbook.pipeline;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.jboss.aerogear.android.ReadFilter;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.pipeline.PipeHandler;
import org.jboss.aerogear.cookbook.model.Car;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class FileHandler implements PipeHandler<Car> {

    private static final String FILE_NAME = "cars.json";
    private static final Gson GSON = new Gson();
    private final Context applicationContext;

    public FileHandler(Context applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    @Override
    public List<Car> onRead(Pipe<Car> requestingPipe) {
        JsonParser parser = new JsonParser();
        BufferedReader carsReader;
        try {
            carsReader = new BufferedReader(new InputStreamReader(applicationContext.getAssets().open(FILE_NAME)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JsonArray carsJson = parser.parse(carsReader).getAsJsonObject().getAsJsonArray("data");
        Car[] cars = GSON.fromJson(carsJson, Car[].class);
        return Arrays.asList(cars);
    }

    @Override
    public List<Car> onReadWithFilter(ReadFilter filter, Pipe<Car> requestingPipe) {
        return onRead(requestingPipe);
    }

    @Override
    public Car onSave(Car item) {
        throw new IllegalAccessError("Not Supported");
    }

    @Override
    public void onRemove(String id) {
        throw new IllegalAccessError("Not Supported");
    }

}