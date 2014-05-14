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
package org.jboss.aerogear.cookbook.push;

import android.support.v7.app.ActionBarActivity;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.jboss.aerogear.cookbook.GuideApplication;
import org.jboss.aerogear.cookbook.R;
import org.jboss.aerogear.cookbook.ProgressFragment;

public class HowToUsePushActivity extends ActionBarActivity implements MessageHandler {

    private static final String TAG = HowToUsePushActivity.class.getSimpleName();

    private Button registerButton;
    private Button unregisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push);

        registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
                registerButton.setEnabled(false);
                register();
            }
        });

        unregisterButton = (Button) findViewById(R.id.unregister);
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
                unregisterButton.setEnabled(false);
                unregister();
            }
        });

        if (getIntent() != null && getIntent().hasExtra("alert")) {
            registered(true);
            onMessage(this, getIntent().getExtras());
        } else {
            displayMessage(getString(R.string.push_label));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Registrations.registerMainThreadHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Registrations.unregisterMainThreadHandler(this);
    }

    @Override
    public void onMessage(Context context, Bundle bundle) {
        String message = getString(R.string.push_message, bundle.getString("sentAt"), bundle.getString("alert"));
        displayMessage(message);
    }

    @Override
    public void onDeleteMessage(Context context, Bundle arg0) {
        // see: https://developer.android.com/reference/com/google/android/gms/gcm/GoogleCloudMessaging.html#MESSAGE_TYPE_DELETED
    }

    @Override
    public void onError() {
        // see: https://developer.android.com/reference/com/google/android/gms/gcm/GoogleCloudMessaging.html#MESSAGE_TYPE_SEND_ERROR
    }

    private void load() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new ProgressFragment())
                .commit();
    }

    private void displayMessage(String message) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new MessageFragment(message))
                .commit();
    }

    private PushRegistrar getRegistrar() {
        return ((GuideApplication) getApplication()).getRegistrar();
    }

    private void register() {
        getRegistrar().register(getApplicationContext(), new Callback<Void>() {
            @Override
            public void onSuccess(Void ignore) {
                Log.i(TAG, getString(R.string.register_successful));
                displayMessage(getString(R.string.register_successful));
                registered(true);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, exception.getMessage(), exception);
                displayMessage(getString(R.string.register_failed));
                registered(false);
            }
        });
    }

    private void unregister() {
        getRegistrar().unregister(getApplicationContext(), new Callback<Void>() {
            @Override
            public void onSuccess(Void ignore) {
                Log.i(TAG, getString(R.string.unregister_successful));
                displayMessage(getString(R.string.unregister_successful));
                unregisterButton.setEnabled(false);
                registered(false);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(TAG, exception.getMessage(), exception);
                displayMessage(getString(R.string.unregister_failed));
                registered(true);
            }
        });
    }

    private void registered(boolean registered) {
        registerButton.setEnabled(!registered);
        unregisterButton.setEnabled(registered);
    }
}
