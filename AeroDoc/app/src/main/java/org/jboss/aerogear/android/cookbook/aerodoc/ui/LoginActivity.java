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
package org.jboss.aerogear.android.cookbook.aerodoc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import org.jboss.aerogear.android.cookbook.aerodoc.AeroDocApplication;
import org.jboss.aerogear.android.cookbook.aerodoc.R;
import org.jboss.aerogear.android.cookbook.aerodoc.model.SaleAgent;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;

import java.nio.charset.Charset;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();
    private AeroDocApplication application;
    private View contentPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        application = (AeroDocApplication) getApplication();

        contentPanel = findViewById(R.id.contentPanel);
        
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        final Button login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidForm(username, password)) {
                    String user = username.getText().toString();
                    String pass = password.getText().toString();
                    login(user, pass);
                }
            }
        });

    }

    public void login(final String user, final String pass) {

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.wait)
                .content(R.string.loging)
                .progress(true, 0)
                .show();

        application.login(user, pass, new Callback<HeaderAndBody>() {
            @Override
            public void onSuccess(HeaderAndBody headerAndBody) {
                String response = new String(headerAndBody.getBody(), Charset.forName("UTF-8"));
                SaleAgent saleAgent = new Gson().fromJson(response, SaleAgent.class);

                application.setSaleAgent(saleAgent);
                application.registerDeviceOnPushServer(user);

                dialog.dismiss();

                startActivity(new Intent(LoginActivity.this, AeroDocActivity.class));

                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, e.getMessage(), e);

                dialog.dismiss();

                Snackbar.make(contentPanel, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private boolean isValidForm(EditText... fields) {
        boolean valid = true;

        for (EditText field : fields) {
            Editable value = field.getText();
            if((value == null) || (value.toString().trim().isEmpty())) {
                field.setError(getString(R.string.cant_be_blank));
                valid = false;
            }
        }

        return valid;
    }



}
