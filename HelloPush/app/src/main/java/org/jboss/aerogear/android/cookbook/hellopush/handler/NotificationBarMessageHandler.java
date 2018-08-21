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
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.fcm.UnifiedPushMessage;
import org.jboss.aerogear.android.cookbook.hellopush.HelloWorldApplication;
import org.jboss.aerogear.android.cookbook.hellopush.R;
import org.jboss.aerogear.android.cookbook.hellopush.activities.MessagesActivity;

import java.util.Map;

public class NotificationBarMessageHandler implements MessageHandler {

    private static final String CHANNEL_ID = "AEROGEAR_PUSH_EXAMPLE";
    private static final String CHANNEL_NAME = "AeroGear Android Push";
    private static final String CHANNEL_DESCRIPTION = "AeroGear Android Push example";

    private static final int NOTIFICATION_ID = 1;

    private static final NotificationBarMessageHandler instance =
            new NotificationBarMessageHandler();

    public static NotificationBarMessageHandler getInstance() {
        return instance;
    }

    @Override
    public void onMessage(Context context, Bundle message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context);
        }

        HelloWorldApplication application = (HelloWorldApplication) context.getApplicationContext();

        application.addMessage(message.getString(UnifiedPushMessage.ALERT_KEY));

        displayMessageOnNotificationBar(context, message);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(Context context) {

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(CHANNEL_DESCRIPTION);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[] {100, 200, 300, 400, 500, 400, 300, 200, 400});

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

    }

    private void displayMessageOnNotificationBar(Context context, Bundle message) {
        String pushMessage = message.getString(UnifiedPushMessage.ALERT_KEY);
        String pushMessageId = message.getString(UnifiedPushMessage.PUSH_MESSAGE_ID);

        Intent intent = new Intent(context, MessagesActivity.class)
                .putExtra(UnifiedPushMessage.ALERT_KEY, pushMessage)
                .putExtra(UnifiedPushMessage.PUSH_MESSAGE_ID, pushMessageId)
                .putExtra(HelloWorldApplication.PUSH_MESSAGE_FROM_BACKGROUND, true);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(pushMessage))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(pushMessage).setContentIntent(contentIntent);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
