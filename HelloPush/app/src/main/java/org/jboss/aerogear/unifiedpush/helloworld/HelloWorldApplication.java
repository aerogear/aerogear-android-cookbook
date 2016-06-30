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
package org.jboss.aerogear.unifiedpush.helloworld;

import android.app.Application;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;
import org.jboss.aerogear.android.unifiedpush.fcm.AeroGearFCMPushRegistrar;
import org.jboss.aerogear.android.unifiedpush.metrics.UnifiedPushMetricsMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelloWorldApplication extends Application {

    public static final String PUSH_REGISTER_NAME = "UNIFIED_PUSH_HELLOWORLD";
    public static final String PUSH_MESSAGE_FROM_BACKGROUND = "PUSH_MESSAGE_FROM_BACKGROUND";

    private List<String> messages;

    @Override
    public void onCreate() {
        super.onCreate();
        messages = new ArrayList<String>();
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(String newMessage) {
        messages.add(newMessage);
    }

    public void sendMetric(UnifiedPushMetricsMessage metricsMessage, Callback<UnifiedPushMetricsMessage> callback) {
        AeroGearFCMPushRegistrar registrar = (AeroGearFCMPushRegistrar)
                RegistrarManager.getRegistrar(PUSH_REGISTER_NAME);
        registrar.sendMetrics(metricsMessage, callback);
    }

}
