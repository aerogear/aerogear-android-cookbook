/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.aerogear.android.cookbook.shootandshare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jboss.aerogear.android.cookbook.shootandshare.R;
import org.jboss.aerogear.android.cookbook.shootandshare.service.UploadService;
import org.jboss.aerogear.android.cookbook.shootandshare.util.FacebookHelper;
import org.jboss.aerogear.android.cookbook.shootandshare.util.GooglePlusHelper;
import org.jboss.aerogear.android.cookbook.shootandshare.util.KeycloakHelper;
import org.jboss.aerogear.android.core.Callback;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = PhotoActivity.class.getSimpleName();
    private String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photo = getIntent().getStringExtra("PHOTO");
        ImageView photoImageView = (ImageView) findViewById(R.id.image);
        Picasso.with(getApplicationContext()).load(photo).into(photoImageView);

        ImageView googlePlus = (ImageView) findViewById(R.id.google_plus);
        googlePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPhotoToGooglePlus();
            }
        });

        ImageView keycloak = (ImageView) findViewById(R.id.keycloak);
        keycloak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPhotoToKeycloak();
            }
        });

        ImageView facebook = (ImageView) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPhotoToFacebook();
            }
        });
    }

    private void sendPhotoToGooglePlus() {
        if (!GooglePlusHelper.isConnected()) {

            GooglePlusHelper.connect(PhotoActivity.this, new Callback() {
                        @Override
                        public void onSuccess(Object o) {
                            sendPhoto(UploadService.PROVIDERS.GOOGLE);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            logAndDisplayAuthenticationError(e, UploadService.PROVIDERS.GOOGLE);
                        }

                    }
            );

        } else {
            sendPhoto(UploadService.PROVIDERS.GOOGLE);
        }
    }

    private void sendPhotoToFacebook() {
        if (!FacebookHelper.isConnected()) {

            FacebookHelper.connect(PhotoActivity.this, new Callback() {
                        @Override
                        public void onSuccess(Object o) {
                            sendPhoto(UploadService.PROVIDERS.FACEBOOK);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            logAndDisplayAuthenticationError(e, UploadService.PROVIDERS.FACEBOOK);
                        }
                    }
            );

        } else {
            sendPhoto(UploadService.PROVIDERS.FACEBOOK);
        }
    }

    private void sendPhotoToKeycloak() {
        if (!KeycloakHelper.isConnected()) {

            KeycloakHelper.connect(PhotoActivity.this, new Callback() {
                        @Override
                        public void onSuccess(Object o) {
                            sendPhoto(UploadService.PROVIDERS.KEYCLOAK);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            logAndDisplayAuthenticationError(e, UploadService.PROVIDERS.KEYCLOAK);
                        }
                    }
            );

        } else {
            sendPhoto(UploadService.PROVIDERS.KEYCLOAK);
        }
    }

    private void sendPhoto(UploadService.PROVIDERS provider) {
        Intent shareIntent = new Intent(PhotoActivity.this, UploadService.class);
        shareIntent.putExtra(UploadService.FILE_URI, photo.replace("file://", ""));
        shareIntent.putExtra(UploadService.PROVIDER, provider.name());
        startService(shareIntent);
    }

    private void logAndDisplayAuthenticationError(Exception e, UploadService.PROVIDERS provider) {
        Log.e(TAG, e.getMessage(), e);
        String message = getString(R.string.authentication_error, provider.name());
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
