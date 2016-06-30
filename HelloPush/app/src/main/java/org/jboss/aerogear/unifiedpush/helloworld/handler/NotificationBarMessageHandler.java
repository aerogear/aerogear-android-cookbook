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
package org.jboss.aerogear.unifiedpush.helloworld.handler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.fcm.UnifiedPushMessage;
import org.jboss.aerogear.unifiedpush.helloworld.HelloWorldApplication;
import org.jboss.aerogear.unifiedpush.helloworld.R;
import org.jboss.aerogear.unifiedpush.helloworld.activities.MessagesActivity;

public class NotificationBarMessageHandler implements MessageHandler {

    public static final int NOTIFICATION_ID = 1;
    private Context context;

    public static final NotificationBarMessageHandler instance = new NotificationBarMessageHandler();

    public NotificationBarMessageHandler() {
    }

    @Override
    public void onMessage(Context context, Bundle bundle) {
        this.context = context;

        String message = bundle.getString(UnifiedPushMessage.ALERT_KEY);

        HelloWorldApplication application = (HelloWorldApplication) context.getApplicationContext();
        application.addMessage(message);

        notify(bundle);
    }

    private void notify(Bundle bundle) {
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        String message = bundle.getString(UnifiedPushMessage.ALERT_KEY);
        String pushMessageId = bundle.getString(UnifiedPushMessage.PUSH_MESSAGE_ID);

        Intent intent = new Intent(context, MessagesActivity.class)
                .addFlags(PendingIntent.FLAG_UPDATE_CURRENT)
                .putExtra(UnifiedPushMessage.ALERT_KEY, message)
                .putExtra(UnifiedPushMessage.PUSH_MESSAGE_ID, pushMessageId)
                .putExtra(HelloWorldApplication.PUSH_MESSAGE_FROM_BACKGROUND, true);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


}
