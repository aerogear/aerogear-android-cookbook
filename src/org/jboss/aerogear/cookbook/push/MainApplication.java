/**
 * JBoss, Home of Professional Open Source Copyright Red Hat, Inc., and
 * individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jboss.aerogear.cookbook.push;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.unifiedpush.PushConfig;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;

public class MainApplication extends Application implements PushConstants {

    private static final String TAG = MainApplication.class.getSimpleName();
    private PushRegistrar registrar;
    private PushConfig config;
    private final Registrations registrations = new Registrations();
    

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            
            config = new PushConfig(new URL(UNIFIED_PUSH_URL), GCM_SENDER_ID);
            config.setVariantID(VARIANT_ID);
            config.setSecret(SECRET);
            config.setAlias(MY_ALIAS);
            
            registrar = registrations.push("registrar", config);
            

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public PushRegistrar getRegistrar() {
        return registrar;
    }

    public void register() {
        registrar.register(getApplicationContext(), new Callback<Void>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSuccess(Void ignore) {
                Toast.makeText(MainApplication.this, "Register successful", Toast.LENGTH_LONG).show();
                Log.i(TAG, "SUCCESS");
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(MainApplication.this, "Register failed", Toast.LENGTH_LONG).show();
                Log.e(TAG, exception.getMessage(), exception);
            }
        });
    }
    
    public void unregister() {
        registrar.unregister(getApplicationContext(), new Callback<Void>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSuccess(Void ignore) {
                Log.i(TAG, "SUCCESS");
                Toast.makeText(MainApplication.this, "Unregister successful", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(MainApplication.this, "Unregister failed", Toast.LENGTH_LONG).show();
                Log.e(TAG, exception.getMessage(), exception);
            }
        });
    }
    
}
