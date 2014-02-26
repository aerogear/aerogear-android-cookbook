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
package org.jboss.aerogear.cookbook.authentication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.authentication.AuthenticationConfig;
import org.jboss.aerogear.android.authentication.AuthenticationModule;
import org.jboss.aerogear.android.authentication.impl.AuthTypes;
import org.jboss.aerogear.android.authentication.impl.Authenticator;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;
import org.jboss.aerogear.android.pipeline.LoaderPipe;
import org.jboss.aerogear.cookbook.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.jboss.aerogear.cookbook.Constants.URL_BASE;

public class HowToUseHttpBasicAuthentication extends Activity {

    private AuthenticationModule authModule;
    private LoaderPipe<String> pipe;

    private ListView listView;
    private Button retriveDataButton;
    private Button clearDataButton;
    private Button loginButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_with_data);

        authModule = createAuthenticatior();
        pipe = createPipe();

        TextView screenTitle = (TextView) findViewById(R.id.screen_title);
        screenTitle.setText(getString(R.string.how_to_use_http_basic_authentication));

        listView = (ListView) findViewById(R.id.list);
        retriveDataButton = (Button) findViewById(R.id.retriveData);
        clearDataButton = (Button) findViewById(R.id.clearData);
        loginButton = (Button) findViewById(R.id.login);
        logoutButton = (Button) findViewById(R.id.logout);


        setListeners();
    }

    private AuthenticationModule createAuthenticatior() {
        AuthenticationConfig authenticationConfig = new AuthenticationConfig();
        authenticationConfig.setAuthType(AuthTypes.HTTP_BASIC);
        authenticationConfig.setLoginEndpoint("/grocery/beers");

        Authenticator authenticator = new Authenticator(URL_BASE);
        authenticator.auth("login", authenticationConfig);

        return authenticator.get("login", this);
    }

    private LoaderPipe createPipe() {
        Pipeline pipeline = null;

        try {
            PipeConfig pipeConfig = new PipeConfig(new URL(URL_BASE), String.class);
            pipeConfig.setName("beer");
            pipeConfig.setEndpoint("/grocery/beers");
            pipeline = new Pipeline(URL_BASE);
            pipeline.pipe(String.class, pipeConfig);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return pipeline.get("beer", this);
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
                authModule.login("john", "123", new LoginAuthCallBack());
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authModule.logout(new LogoutAuthCallBack());
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
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, beerList));
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

    private static class LoginAuthCallBack extends AbstractActivityCallback<HeaderAndBody> {
        @Override
        public void onSuccess(HeaderAndBody data) {
            HowToUseHttpBasicAuthentication activity = (HowToUseHttpBasicAuthentication) getActivity();
            activity.logged(true);
        }

        @Override
        public void onFailure(Exception e) {
            HowToUseHttpBasicAuthentication activity = (HowToUseHttpBasicAuthentication) getActivity();
            activity.displayMessage(e.getMessage());
        }
    }

    private static class LogoutAuthCallBack extends AbstractActivityCallback<Void> {
        @Override
        public void onSuccess(Void data) {
            HowToUseHttpBasicAuthentication activity = (HowToUseHttpBasicAuthentication) getActivity();
            activity.logged(false);
        }

        @Override
        public void onFailure(Exception e) {
            HowToUseHttpBasicAuthentication activity = (HowToUseHttpBasicAuthentication) getActivity();
            activity.displayMessage(e.getMessage());
        }
    }

}
