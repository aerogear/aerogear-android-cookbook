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
package org.jboss.aerogear.unifiedpush.helloworld.callback;

import android.util.Log;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.metrics.UnifiedPushMetricsMessage;

public class MetricsCallback implements Callback<UnifiedPushMetricsMessage> {

    private static final String TAG = MetricsCallback.class.getName();

    @Override
    public void onSuccess(UnifiedPushMetricsMessage metricsMessage) {
        Log.i(TAG, "The message " + metricsMessage.getMessageId() + " was marked as opened");
    }

    @Override
    public void onFailure(Exception e) {
        Log.e(TAG, e.getMessage(), e);
    }
}
