package net.saga.oauthtestsing.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.authorization.AuthzConfig;
import org.jboss.aerogear.android.impl.authz.AGAuthzService;
import org.jboss.aerogear.android.impl.authz.AGRestAuthzModule;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        AuthzConfig conf = null;
        try {
            conf = new AuthzConfig(new URL("https://accounts.google.com"), "restMod");
            conf.setAccessTokenEndpoint("/o/oauth2/token");
            conf.setAuthzEndpoint("/o/oauth2/auth");
            conf.setAccountId("gemAccounts");
            conf.getAdditionalAuthorizationParams().add(Pair.create("access_type", "offline"));
            conf.setClientId("374822310857-7926b3e6qugkdf2anmm51hkrcr8o7kj2.apps.googleusercontent.com");
            conf.setClientSecret("g6R5mL2MwR_ofdQCXLpKT21L");
            conf.setRedirectURL("org.aerogear.googledrive:/oauth2callback");
            conf.setScopes(new ArrayList<String>() {{
                add("https://www.googleapis.com/auth/drive");
            }});
            final AGRestAuthzModule module = new AGRestAuthzModule(conf);

            module.requestAccess("state", this, new Callback<String>() {
                @Override
                public void onSuccess(String o) {
                    Toast.makeText(getApplicationContext(), o, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static class OAuthReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getDataString(), Toast.LENGTH_LONG).show();
        }
    }


}
