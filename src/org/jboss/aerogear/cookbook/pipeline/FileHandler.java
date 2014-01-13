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

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.jboss.aerogear.android.ReadFilter;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.pipeline.PipeHandler;
import org.jboss.aerogear.cookbook.model.Developer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class FileHandler implements PipeHandler<Developer> {

    private static final String FILE_NAME = "developers.json";
    private static final Gson GSON = new Gson();
    private final Context applicationContext;

    public FileHandler(Context applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    @Override
    public List<Developer> onRead(Pipe<Developer> requestingPipe) {
        JsonParser parser = new JsonParser();
        BufferedReader carsReader;
        try {
            carsReader = new BufferedReader(new InputStreamReader(applicationContext.getAssets().open(FILE_NAME)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JsonArray json = parser.parse(carsReader).getAsJsonObject().getAsJsonArray("data");
        Developer[] developers = GSON.fromJson(json, Developer[].class);
        return Arrays.asList(developers);
    }

    @Override
    public List<Developer> onReadWithFilter(ReadFilter filter, Pipe<Developer> requestingPipe) {
        return onRead(requestingPipe);
    }

    @Override
    public Developer onSave(Developer item) {
        throw new IllegalAccessError("Not Supported");
    }

    @Override
    public void onRemove(String id) {
        throw new IllegalAccessError("Not Supported");
    }

}