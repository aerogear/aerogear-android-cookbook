package org.jboss.aerogear.android.cookbook.syncdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import org.jboss.aerogear.android.cookbook.syncdemo.helper.KeycloakHelper;
import org.jboss.aerogear.android.core.Callback;


public class SigningInActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

    }

    @Override
    protected void onStart() {
        super.onStart();
        KeycloakHelper.connect(SigningInActivity.this, new Callback() {
            @Override
            public void onSuccess(Object o) {
                KeycloakHelper.saveUser("ignore", new Callback() {
                    @Override
                    public void onSuccess(Object o) {
                        Intent i = new Intent(getApplicationContext(), DocsActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }, SigningInActivity.this);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
