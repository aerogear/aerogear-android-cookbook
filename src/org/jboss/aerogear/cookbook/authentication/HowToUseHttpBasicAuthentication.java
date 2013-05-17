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
import org.jboss.aerogear.android.authentication.impl.AuthTypes;
import org.jboss.aerogear.android.authentication.impl.Authenticator;
import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.pipeline.AbstractActivityCallback;
import org.jboss.aerogear.cookbook.R;

public class HowToUseHttpBasicAuthentication extends Activity {

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
        authenticationConfig.setAuthType(AuthTypes.HTTP_BASIC);
        authenticationConfig.setLoginEndpoint("/autobots");
        authModule = authenticator.auth("login", authenticationConfig);

        status  = (TextView) findViewById(R.id.status);
        login   = (Button)   findViewById(R.id.login);
        logout  = (Button)   findViewById(R.id.logout);

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
                        Toast.makeText(HowToUseHttpBasicAuthentication.this, "", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(HowToUseHttpBasicAuthentication.this, "", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void logged(final boolean logged) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                status.setText(getString(logged ? R.string.logged : R.string.notlogged));
                login.setEnabled(!logged);
                logout.setEnabled(logged);
            }
        });
    }

}
