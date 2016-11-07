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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jboss.aerogear.android.authentication.AuthenticationManager;
import org.jboss.aerogear.android.authentication.AuthenticationModule;
import org.jboss.aerogear.android.authentication.basic.HttpBasicAuthenticationConfiguration;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.LoaderPipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class HowToUseHttpBasicAuthentication extends Activity {

    private AuthenticationModule authModule;
    private LoaderPipe<String> pipe;

    private ListView listView;
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
        screenTitle.setText(getString(R.string.http_basic_authentication));

        listView = (ListView) findViewById(R.id.list);
        retriveDataButton = (Button) findViewById(R.id.retriveData);
        clearDataButton = (Button) findViewById(R.id.clearData);
        loginButton = (Button) findViewById(R.id.login);
        logoutButton = (Button) findViewById(R.id.logout);


        setListeners();
    }

    private AuthenticationModule createAuthenticatior() {
        HttpBasicAuthenticationConfiguration authenticationConfig;
        try {
            authenticationConfig = AuthenticationManager.config("login", HttpBasicAuthenticationConfiguration.class)
                    .baseURL(new URL(Constants.URL_BASE));
        } catch (MalformedURLException e) {
            displayMessage(getString(R.string.URLException));
            finish();
            return null;
        }

        return authenticationConfig.asModule();
    }

    private LoaderPipe createPipe(AuthenticationModule authModule) {


        try {
            RestfulPipeConfiguration pipeConfig = PipeManager.config("beer", RestfulPipeConfiguration.class)
                    .module(authModule)
                    .withUrl(new URL(Constants.URL_BASE + "/grocery/beers"));

            pipeConfig.forClass(String.class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            displayMessage(getString(R.string.URLException));
            finish();
            return null;
        }

        return PipeManager.getPipe("beer", this);
    }

    private void setListeners() {
        retriveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retriveBeers();
            }
        });

        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBeerList();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authModule.login("john", "123", new LoginAuthCallBack(HowToUseHttpBasicAuthentication.this));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authModule.logout(new LogoutAuthCallBack(HowToUseHttpBasicAuthentication.this));
            }
        });
    }

    // Button Actions ---------------------------------------------

    private void retriveBeers() {
        pipe.read(new RetriveBeerCallback());
    }

    private void clearBeerList() {
        listView.setAdapter(null);
    }

    // UIThread callback delegate ---------------------------------

    public void logged(boolean logged) {
        if (logged) {
            retriveBeers();
        } else {
            clearBeerList();
        }

        displayMessage(logged ? getString(R.string.login_successful) : getString(R.string.logout_successful));
        loginButton.setEnabled(!logged);
        logoutButton.setEnabled(logged);
    }

    private void displayBeers(List<String> beerList) {
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, beerList));
    }

    private void displayMessage(String message) {
        Toast.makeText(HowToUseHttpBasicAuthentication.this, message, Toast.LENGTH_SHORT).show();
    }

    // Callbacks --------------------------------------------------

    private static class RetriveBeerCallback extends AbstractActivityCallback<List<String>> {
        @Override
        public void onSuccess(List<String> data) {
            HowToUseHttpBasicAuthentication activity = (HowToUseHttpBasicAuthentication) getActivity();
            activity.displayBeers(data);
        }

        @Override
        public void onFailure(Exception e) {
            HowToUseHttpBasicAuthentication activity = (HowToUseHttpBasicAuthentication) getActivity();
            activity.displayMessage(e.getMessage());
        }
    }

    private static class LoginAuthCallBack implements Callback<HeaderAndBody> {
        private final HowToUseHttpBasicAuthentication activity;

        private LoginAuthCallBack(HowToUseHttpBasicAuthentication activity) {
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
        private final HowToUseHttpBasicAuthentication activity;

        private LogoutAuthCallBack(HowToUseHttpBasicAuthentication activity) {
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
                    activity.displayMessage(e.getMessage());
                }
            });
        }
    }

}
