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
package org.jboss.aerogear.android.cookbook.authexamples;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jboss.aerogear.android.authentication.AuthenticationManager;
import org.jboss.aerogear.android.authentication.AuthenticationModule;
import org.jboss.aerogear.android.authentication.digest.HttpDigestAuthenticationConfiguration;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HowToUseDigestAuthentication extends AppCompatActivity {

    private AuthenticationModule authModule;
    private LoaderPipe<String> pipe;

    private ListView bacons;
    private Button retriveDataButton;
    private Button clearDataButton;
    private Button loginButton;
    private Button logoutButton;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_with_data);

        authModule = createAuthenticatior();
        pipe = createPipe(authModule);

        TextView screenTitle = (TextView) findViewById(R.id.screen_title);
        screenTitle.setText(getString(R.string.digest_authentication));

        bacons = (ListView) findViewById(R.id.list);
        retriveDataButton = (Button) findViewById(R.id.retriveData);
        clearDataButton = (Button) findViewById(R.id.clearData);
        loginButton = (Button) findViewById(R.id.login);
        logoutButton = (Button) findViewById(R.id.logout);

        setListeners();
    }

    private AuthenticationModule createAuthenticatior() {
        HttpDigestAuthenticationConfiguration authenticationConfig;
        try {
            authenticationConfig = AuthenticationManager.config("digest", HttpDigestAuthenticationConfiguration.class)
                    .baseURL(new URL(Constants.URL_BASE))
                    .logoutEndpoint("")
                    .loginEndpoint("/grocery/bacons");
        } catch (MalformedURLException e) {
            displayMessage(getString(R.string.URLException));
            finish();
            return null;
        }

        return authenticationConfig.asModule();
    }

    private LoaderPipe createPipe(AuthenticationModule module) {


        try {
            RestfulPipeConfiguration pipeConfig = PipeManager.config("bacon", RestfulPipeConfiguration.class)
                    .module(authModule)
                    .withUrl(new URL(Constants.URL_BASE + "/grocery/bacons"));

            pipeConfig.forClass(String.class);

        } catch (MalformedURLException e) {
            displayMessage(getString(R.string.URLException));
            finish();
            return null;
        }

        return PipeManager.getPipe("bacon", this);
    }

    private void setListeners() {
        retriveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retriveBacon();
            }
        });

        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBaconList();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authModule.login("agnes", "123", new LoginAuthCallBack(HowToUseDigestAuthentication.this));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authModule.logout(new LogoutAuthCallBack(HowToUseDigestAuthentication.this));
            }
        });
    }

    // Button Actions ---------------------------------------------

    private void retriveBacon() {
        pipe.reset(); // Don't cache data
        pipe.read(new RetriveBaconCallback());
    }

    private void clearBaconList() {
        bacons.setAdapter(null);
    }

    // UIThread callback delegate ---------------------------------

    public void logged(boolean logged) {
        if (logged) {
            retriveBacon();
        } else {
            clearBaconList();
        }

        displayMessage(logged ? getString(R.string.login_successful) : getString(R.string.logout_successful));
        loginButton.setEnabled(!logged);
        logoutButton.setEnabled(logged);
    }

    private void displayBacons(List<String> baconList) {
        bacons.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, baconList));
    }

    private void displayMessage(String message) {
        Toast.makeText(HowToUseDigestAuthentication.this, message, Toast.LENGTH_SHORT).show();
    }

    // Callbacks --------------------------------------------------

    private static class RetriveBaconCallback extends AbstractActivityCallback<List<String>> {
        @Override
        public void onSuccess(List<String> data) {
            HowToUseDigestAuthentication activity = (HowToUseDigestAuthentication) getActivity();
            activity.displayBacons(data);
        }

        @Override
        public void onFailure(Exception e) {
            HowToUseDigestAuthentication activity = (HowToUseDigestAuthentication) getActivity();
            activity.displayMessage(e.getMessage());
        }
    }

    private static class LoginAuthCallBack implements Callback<HeaderAndBody> {
        private final HowToUseDigestAuthentication activity;

        private LoginAuthCallBack(HowToUseDigestAuthentication activity) {
            this.activity = activity;
        }


        @Override
        public void onSuccess(HeaderAndBody data) {
            activity.handler.post(new Runnable() {
                @Override
                public void run() {
                    activity.logged(true);
                }
            });
        }

        @Override
        public void onFailure(final Exception e) {
            activity.handler.post(new Runnable() {
                @Override
                public void run() {
                    activity.displayMessage(e.getMessage());
                }
            });
        }
    }

    private static class LogoutAuthCallBack implements Callback<Void> {
        private final HowToUseDigestAuthentication activity;

        private LogoutAuthCallBack(HowToUseDigestAuthentication activity) {
            this.activity = activity;
        }

        @Override
        public void onSuccess(Void data) {
            activity.handler.post(new Runnable() {
                @Override
                public void run() {
                    activity.logged(false);
                }
            });
        }

        @Override
        public void onFailure(final Exception e) {
            activity.handler.post(new Runnable() {
                @Override
                public void run() {
                    //There is no serverside logout so we get a error.
                    // Logout does dump the credentials however.
                    //see https://issues.jboss.org/browse/AGDROID-349
                    activity.logged(false);
                }
            });
        }
    }

}
