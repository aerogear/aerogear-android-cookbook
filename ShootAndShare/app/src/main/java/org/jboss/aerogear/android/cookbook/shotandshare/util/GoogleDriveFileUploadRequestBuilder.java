/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.android.cookbook.shotandshare.util;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.jboss.aerogear.android.impl.pipeline.MultipartRequestBuilder;
import org.jboss.aerogear.android.pipeline.TypeAndStream;
import org.jboss.aerogear.android.cookbook.shotandshare.model.PhotoHolder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

public class GoogleDriveFileUploadRequestBuilder extends MultipartRequestBuilder<PhotoHolder> {

    private static final String lineEnd = "\r\n";
    private static final String twoHyphens = "--";
    private static final String TAG = GoogleDriveFileUploadRequestBuilder.class.getSimpleName();
    private final String boundary = UUID.randomUUID().toString();
    private final String CONTENT_TYPE = "multipart/related; boundary=" + boundary;


    @Override
    public byte[] getBody(PhotoHolder data) {
        try {
            ByteArrayOutputStream binaryStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(binaryStream);
            File value = data.getImage();

            TypeAndStream file = new TypeAndStream(getMimeType(value),
                                                   value.getName(),
                                                   new FileInputStream(value));

            writeMetaData(dataOutputStream, data);

            writeFile(dataOutputStream, file);

            return binaryStream.toByteArray();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }

    private void writeFile(DataOutputStream dataOutputStream, TypeAndStream file) throws IOException {
        TypeAndStream type = file;
        String name = file.getFileName();
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + type.getFileName() + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: " + type.getMimeType() + lineEnd);
        dataOutputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        int b;
        while ((b = type.getInputStream().read()) != -1) {
            dataOutputStream.write(b);
        }
        dataOutputStream.writeBytes(lineEnd);

        dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    }


    private void writeMetaData(DataOutputStream dataOutputStream, PhotoHolder data ) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("title", new JsonPrimitive(data.getTitle()));
        String json = jsonObject.toString();

        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"metadata\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: application/json; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(json + lineEnd);
    }

    private String getMimeType(File file) throws MalformedURLException {
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toURL().toString());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }
}
