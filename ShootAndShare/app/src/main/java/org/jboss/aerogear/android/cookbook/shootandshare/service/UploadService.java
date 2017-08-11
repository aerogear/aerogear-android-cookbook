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
package org.jboss.aerogear.android.cookbook.shootandshare.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.jboss.aerogear.android.cookbook.shootandshare.R;
import org.jboss.aerogear.android.cookbook.shootandshare.model.PhotoHolder;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.PipeManager;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public final class UploadService extends Service {

    private static final String TAG = UploadService.class.getSimpleName();

    public static final String FILE_URI = "UploadService.FILE_URI";
    public static final String PROVIDER = "UploadService.PROVIDER";
    private static final String CHANNEL_ID = "shootnshare_push_channel";

    public enum PROVIDERS {GOOGLE, KEYCLOAK, FACEBOOK}

    private static final AtomicInteger notificationCount = new AtomicInteger(1);

    private final Handler handler;

    public UploadService() {
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    @SuppressWarnings("unchecked")
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        handler.post(() -> {
            int id = 0;
            try {

                Bundle extras = intent.getExtras();

                String fileName = extras.getString(FILE_URI);
                String providerName = extras.getString(PROVIDER);

                if (fileName == null) {
                    displayErrorNotification("No file provided", 0);
                    return;
                }

                if (providerName == null) {
                    displayErrorNotification("No provider selected", 0);
                    return;
                }

                PROVIDERS provider = PROVIDERS.valueOf(providerName);
                File file = new File(fileName);
                id = displayUploadNotification(fileName);

                switch (provider) {

                    case GOOGLE:
                        PipeManager.getPipe("gp-upload").save(new PhotoHolder(file), new UploadCallback(id));
                        break;
                    case KEYCLOAK:
                        PipeManager.getPipe("kc-upload").save(new PhotoHolder(file), new UploadCallback(id));
                        break;
                    case FACEBOOK:
                        PipeManager.getPipe("fb-upload").save(new PhotoHolder(file), new UploadCallback(id));
                        break;
                }

            } catch (Exception e) {
                displayErrorNotification(e.getMessage(), id);
            }

        });

        return START_NOT_STICKY;
    }

    private int displayUploadNotification(String fileName) {

        Notification.Builder builder;

        // because versions >= O require a channel for notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("Uploading...");
        builder.setContentText("Uploading " + fileName);
        builder.setProgress(0, 0, true);
        builder.setOngoing(true);

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int id = notificationCount.getAndIncrement();

        notificationManager.notify(id, notification);

        return id;

    }

    private void displayErrorNotification(String message, int id) {

        if (id > 0) {
            clearUploadNotification(id);
        }

        Notification.Builder builder;

        // because versions >= O require a channel for notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("Upload Error");
        builder.setContentText(message);

        builder.setOngoing(false);

        Notification notification = builder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationCount.getAndIncrement(), notification);

    }

    private void clearUploadNotification(int id) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            throw new IllegalStateException("This function should not be called on < Android O");
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);

        // The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

        // Configure the notification channel.
        channel.setDescription(description);
        channel.enableLights(true);

        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(channel);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class UploadCallback implements Callback<PhotoHolder> {

        private final int id;

        private UploadCallback(int id) {
            this.id = id;
        }


        @Override
        public void onSuccess(PhotoHolder photoHolder) {
            clearUploadNotification(id);
        }

        @Override
        public void onFailure(Exception e) {
            Log.e(TAG, e.getMessage(), e);
            displayErrorNotification(e.getMessage(), id);
        }
    }


}
