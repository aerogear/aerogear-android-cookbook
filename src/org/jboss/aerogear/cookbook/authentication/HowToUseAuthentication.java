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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.authentication.AuthenticationConfig;
import org.jboss.aerogear.android.authentication.AuthenticationModule;
import org.jboss.aerogear.android.authentication.impl.Authenticator;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;
import org.jboss.aerogear.cookbook.R;

public class HowToUseAuthentication extends Activity {

    private AuthenticationModule authModule;
    private String baseURL = "http://controller-aerogear.rhcloud.com/aerogear-controller-demo";

    private TextView status;
    private Button login;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);

        Authenticator authenticator = new Authenticator(baseURL);
        AuthenticationConfig authenticationConfig = new AuthenticationConfig();
        authenticationConfig.setLoginEndpoint("/login");
        authenticationConfig.setLogoutEndpoint("/logout");
        authModule = authenticator.auth("login", authenticationConfig);

        status = (TextView) findViewById(R.id.status);
        login = (Button) findViewById(R.id.login);
        logout = (Button) findViewById(R.id.logout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authModule.login("john", "123", new AbstractActivityCallback<HeaderAndBody>() {
                    @Override
                    public void onSuccess(HeaderAndBody data) {
                        logged(true);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        displayError(e.getMessage());
                    }
                });
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authModule.logout(new Callback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        logged(false);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        displayError(e.getMessage());
                    }
                });
            }
        });
    }

    private void logged(final boolean logged) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText(getString(logged ? R.string.logged : R.string.notlogged));
                login.setEnabled(!logged);
                logout.setEnabled(logged);
            }
        });
    }

    private void displayError(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HowToUseAuthentication.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

}
