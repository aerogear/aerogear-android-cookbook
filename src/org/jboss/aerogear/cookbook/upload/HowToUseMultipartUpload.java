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
package org.jboss.aerogear.cookbook.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.impl.pipeline.MultipartRequestBuilder;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.cookbook.Constants;
import org.jboss.aerogear.cookbook.R;
import org.jboss.aerogear.cookbook.model.AvatarRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import org.jboss.aerogear.android.impl.pipeline.GsonResponseParser;
import org.jboss.aerogear.cookbook.model.Avatar;
import org.jboss.aerogear.cookbook.model.AvatarResponse;

public class HowToUseMultipartUpload extends Activity {

    private int requestCode = 1;
    private ImageView image;
    private TextView name;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

        image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, requestCode);
            }
        });

        name = (TextView) findViewById(R.id.name);

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == RESULT_OK) && (requestCode == this.requestCode)) {
            Uri imageURI = data.getData();
            image.setImageURI(imageURI);
        }
    }

    private void sendImage() {
        try {
            progressDialog = ProgressDialog.show(HowToUseMultipartUpload.this,
                    "Sending...", "Wait, the data is seding", true);

            URL url = new URL(Constants.URL_BASE);

            PipeConfig config = new PipeConfig(url, Avatar.class);
            config.setRequestBuilder(new MultipartRequestBuilder<Avatar<InputStream>>());
            config.setResponseParser(new GsonResponseParser(getResponseGsonBuilder().create()));
            config.setEndpoint("upload");

            Pipeline pipeline = new Pipeline(url);
            pipeline.pipe(Avatar.class, config);

            LoaderPipe<Avatar> pipe = pipeline.get("avatar", this);
            pipe.save(createAvatar(), new SaveCallBack());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void displaySuccess() {
        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
        image.setImageResource(R.drawable.noimage);
        name.setText("");
        progressDialog.dismiss();
    }

    public void displayError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
    }

    private AvatarRequest createAvatar() {
        AvatarRequest avatar = new AvatarRequest();
        avatar.setName(name.getText().toString());

        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bitmapdata);

        avatar.setPhoto(inputStream);

        return avatar;
    }

    private static class SaveCallBack extends AbstractActivityCallback<Avatar> {

        public SaveCallBack() {
            super(SaveCallBack.class);
        }

        @Override
        public void onSuccess(Avatar data) {
            HowToUseMultipartUpload activity = (HowToUseMultipartUpload) getActivity();
            activity.displaySuccess();
        }

        @Override
        public void onFailure(Exception e) {
            HowToUseMultipartUpload activity = (HowToUseMultipartUpload) getActivity();
            activity.displayError(e);
        }
    }

    private static GsonBuilder getResponseGsonBuilder() {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Avatar.class, new JsonDeserializer() {

            @Override
            public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

                String name = json.getAsJsonObject().get("name").getAsString();
                String photo = json.getAsJsonObject().get("photo").getAsString();

                AvatarResponse response = new AvatarResponse();

                response.setName(name);
                response.setPhoto(photo);

                return response;
            }
        });

        return builder;
    }

}
