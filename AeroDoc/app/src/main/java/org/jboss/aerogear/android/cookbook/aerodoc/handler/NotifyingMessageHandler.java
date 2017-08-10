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
package org.jboss.aerogear.android.cookbook.aerodoc.handler;

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
import org.jboss.aerogear.android.cookbook.aerodoc.R;
import org.jboss.aerogear.android.cookbook.aerodoc.ui.MainActivity;

public class NotifyingMessageHandler implements MessageHandler {

    public static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "aerodoc_push_channel";
    private Context ctx;

    public static final NotifyingMessageHandler instance = new NotifyingMessageHandler();

    public NotifyingMessageHandler() {
    }

    @Override
    public void onMessage(Context context, Bundle message) {
        ctx = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        sendNotification(message.getString("alert"));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            throw new IllegalStateException("This function should not be called on < Android O");
        }

        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        // The user-visible name of the channel.
        CharSequence name = ctx.getString(R.string.channel_name);

        // The user-visible description of the channel.
        String description = ctx.getString(R.string.channel_description);
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


    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(ctx, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra("alert", msg);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx, CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("AeroGear Push Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
