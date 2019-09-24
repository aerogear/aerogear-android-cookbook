/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.android.cookbook.hellopush.handler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;

import org.jboss.aerogear.android.cookbook.hellopush.HelloWorldApplication;
import org.jboss.aerogear.android.cookbook.hellopush.R;
import org.jboss.aerogear.android.cookbook.hellopush.activities.MessagesActivity;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.fcm.UnifiedPushMessage;

public class NotificationBarMessageHandler implements MessageHandler {

    private static final String CHANNEL_ID = "aerogear-channel";
    private static final int NOTIFICATION_ID = 1;

    public static final NotificationBarMessageHandler instance = new NotificationBarMessageHandler();

    // This should be public to be used on AndroidManifest.xml
    @SuppressWarnings("WeakerAccess")
    public NotificationBarMessageHandler() {
    }

    @Override
    public void onMessage(Context context, Bundle bundle) {

        String message = bundle.getString(UnifiedPushMessage.ALERT_KEY);

        HelloWorldApplication application = (HelloWorldApplication) context.getApplicationContext();
        application.addMessage(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context);
        }

        notify(context, bundle);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(Context context) {

        String name = "AeroGear";
        String description = "AeroGear Android Cookbook";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager systemService = context.getSystemService(NotificationManager.class);
        systemService.createNotificationChannel(channel);

    }

    private void notify(Context context, Bundle bundle) {

        String message = bundle.getString(UnifiedPushMessage.ALERT_KEY);
        String pushMessageId = bundle.getString(UnifiedPushMessage.PUSH_MESSAGE_ID);

        Intent intent = new Intent(context, MessagesActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra(UnifiedPushMessage.ALERT_KEY, message)
                .putExtra(UnifiedPushMessage.PUSH_MESSAGE_ID, pushMessageId)
                .putExtra(HelloWorldApplication.PUSH_MESSAGE_FROM_BACKGROUND, true);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context, 0, intent, 0);

        Builder builder = new Builder(context, CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

}
