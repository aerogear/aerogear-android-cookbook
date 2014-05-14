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
import org.jboss.aerogear.android.ReadFilter;
import org.jboss.aerogear.android.pipeline.Pipe;
import org.jboss.aerogear.android.pipeline.PipeHandler;
import org.jboss.aerogear.cookbook.model.Developer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.aerogear.android.http.HeaderAndBody;

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
        throw new IllegalAccessError("Deprecated");
    }

    @Override
    public List<Developer> onReadWithFilter(ReadFilter filter, Pipe<Developer> requestingPipe) {
        throw new IllegalAccessError("Deprecated");
    }

    @Override
    public Developer onSave(Developer item) {
        throw new IllegalAccessError("Not Supported");
    }

    @Override
    public void onRemove(String id) {
        throw new IllegalAccessError("Not Supported");
    }

    @Override
    public HeaderAndBody onRawRead(Pipe<Developer> requestingPipe) {
        try {
            BufferedReader carsReader;
            try {
                carsReader = new BufferedReader(new InputStreamReader(applicationContext.getAssets().open(FILE_NAME)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            int read = -1;
            while ((read = carsReader.read()) != -1) {
                bytes.write(read);
            }

            return new HeaderAndBody(bytes.toByteArray(), new HashMap<String, Object>());

        } catch (IOException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

    }

    @Override
    public HeaderAndBody onRawReadWithFilter(ReadFilter filter, Pipe<Developer> requestingPipe) {
        return onRawRead(requestingPipe);
    }

    @Override
    public HeaderAndBody onRawSave(String id, byte[] item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
