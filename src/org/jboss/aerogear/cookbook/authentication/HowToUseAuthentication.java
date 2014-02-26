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
import org.jboss.aerogear.cookbook.Constants;
import org.jboss.aerogear.cookbook.R;

public class HowToUseAuthentication extends Activity {

    private AuthenticationModule authModule;

    private TextView statusLabel;
    private Button loginButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);

        authModule = createAuthenticatior();

        statusLabel = (TextView) findViewById(R.id.status);
        loginButton = (Button) findViewById(R.id.login);
        logoutButton = (Button) findViewById(R.id.logout);

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

    private AuthenticationModule createAuthenticatior() {
        AuthenticationConfig authenticationConfig = new AuthenticationConfig();
        authenticationConfig.setLoginEndpoint("/auth/login");
        authenticationConfig.setLogoutEndpoint("/auth/logout");

        Authenticator authenticator = new Authenticator(Constants.URL_BASE);
        authenticator.auth("login", authenticationConfig);

        return authenticator.get("login", this);
    }

    // UIThread callback delegate ---------------------------------

    public void logged(boolean logged) {
        displayStatus(logged ? getString(R.string.logged) : getString(R.string.notlogged));
        loginButton.setEnabled(!logged);
        logoutButton.setEnabled(logged);
    }

    private void displayStatus(String stauts) {
        statusLabel.setText(stauts);
    }

    private void displayMessage(String message) {
        Toast.makeText(HowToUseAuthentication.this, message, Toast.LENGTH_LONG).show();
    }

    // Callbacks --------------------------------------------------

    private static class LoginAuthCallBack extends AbstractActivityCallback<HeaderAndBody> {
        @Override
        public void onSuccess(HeaderAndBody data) {
            HowToUseAuthentication activity = (HowToUseAuthentication) getActivity();
            activity.logged(true);
        }

        @Override
        public void onFailure(Exception e) {
            HowToUseAuthentication activity = (HowToUseAuthentication) getActivity();
            activity.displayMessage(e.getMessage());
        }
    }

    private static class LogoutAuthCallBack extends AbstractActivityCallback<Void> {
        @Override
        public void onSuccess(Void data) {
            HowToUseAuthentication activity = (HowToUseAuthentication) getActivity();
            activity.logged(false);
        }

        @Override
        public void onFailure(Exception e) {
            HowToUseAuthentication activity = (HowToUseAuthentication) getActivity();
            activity.displayMessage(e.getMessage());
        }
    }

}
