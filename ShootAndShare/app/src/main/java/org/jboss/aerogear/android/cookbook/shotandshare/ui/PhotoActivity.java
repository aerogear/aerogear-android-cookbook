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

package org.jboss.aerogear.android.cookbook.shotandshare.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.cookbook.shotandshare.R;
import org.jboss.aerogear.android.cookbook.shotandshare.service.UploadService;
import org.jboss.aerogear.android.cookbook.shotandshare.util.FacebookHelper;
import org.jboss.aerogear.android.cookbook.shotandshare.util.GooglePlusHelper;
import org.jboss.aerogear.android.cookbook.shotandshare.util.KeycloakHelper;

public class PhotoActivity extends ActionBarActivity {

    private static final String TAG = PhotoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

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

    @Override
    protected void onResume() {
        super.onResume();

        String photo = getIntent().getStringExtra("PHOTO");
        Bitmap bitmap = BitmapFactory.decodeFile(photo);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(bitmap);
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
        shareIntent.putExtra(UploadService.FILE_URI, getIntent().getStringExtra("PHOTO"));
        shareIntent.putExtra(UploadService.PROVIDER, provider.name());
        startService(shareIntent);
    }

    private void logAndDisplayAuthenticationError(Exception e, UploadService.PROVIDERS provider) {
        Log.e(TAG, e.getMessage(), e);
        String message = getString(R.string.authentication_error, provider.name());
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
