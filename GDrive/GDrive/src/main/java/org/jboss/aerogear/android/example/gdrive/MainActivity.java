package org.jboss.aerogear.android.example.gdrive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.Pipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.rest.gson.GsonResponseParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.jboss.aerogear.android.example.gdrive.Constants.AUTHZ_ACCOUNT_ID;
import static org.jboss.aerogear.android.example.gdrive.Constants.AUTHZ_CLIENT_ID;
import static org.jboss.aerogear.android.example.gdrive.Constants.AUTHZ_CLIENT_SECRET;
import static org.jboss.aerogear.android.example.gdrive.Constants.AUTHZ_ENDPOINT;
import static org.jboss.aerogear.android.example.gdrive.Constants.AUTHZ_REDIRECT_URL;
import static org.jboss.aerogear.android.example.gdrive.Constants.AUTHZ_TOKEN_ENDPOINT;
import static org.jboss.aerogear.android.example.gdrive.Constants.AUTHZ_URL;

public class MainActivity extends ActionBarActivity {

    private AuthzModule authzModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.authz();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (authzModule.isAuthorized()) {
            retrieveFiles();
        } else {
            authz();
        }
        return super.onOptionsItemSelected(item);
    }

    private void authz() {
        try {

            authzModule = AuthorizationManager.config("GoogleDriveAuthz", OAuth2AuthorizationConfiguration.class)
                    .setBaseURL(new URL(AUTHZ_URL))
                    .setAuthzEndpoint(AUTHZ_ENDPOINT)
                    .setAccessTokenEndpoint(AUTHZ_TOKEN_ENDPOINT)
                    .setAccountId(AUTHZ_ACCOUNT_ID)
                    .setClientId(AUTHZ_CLIENT_ID)
                    .setClientSecret(AUTHZ_CLIENT_SECRET)
                    .setRedirectURL(AUTHZ_REDIRECT_URL)
                    .setScopes(Arrays.asList("https://www.googleapis.com/auth/drive"))
                    .addAdditionalAuthorizationParam((Pair.create("access_type", "offline")))
                    .asModule();

            authzModule.requestAccess(this, new Callback<String>() {
                @Override
                public void onSuccess(String o) {
                    Log.d("TOKEN ++ ", o);
                    retrieveFiles();
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

    private void retrieveFiles() {

        displayLoading();

        URL googleDriveURL = null;
        try {
            googleDriveURL = new URL("https://www.googleapis.com/drive/v2/files");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        GsonResponseParser gsonResponseParser = new GsonResponseParser();
        gsonResponseParser.getMarshallingConfig().setDataRoot("items");

        PipeManager.config("files", RestfulPipeConfiguration.class)
                .withUrl(googleDriveURL)
                .module(authzModule)
                .responseParser(gsonResponseParser)
                .forClass(Files.class);

        Pipe<Files> documentsPipe = PipeManager.getPipe("files", this);
        documentsPipe.read(new Callback<List<Files>>() {
            @Override
            public void onSuccess(final List<Files> fileses) {
                Toast.makeText(getApplicationContext(), getString(R.string.fetched, fileses.size()), Toast.LENGTH_LONG).show();
                displayDriveFiles(fileses);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("EXCEPTION", e.getMessage(), e);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }

    private void displayDriveFiles(List<Files> fileses) {
        DriveFragment driveFragment = new DriveFragment(fileses);
        displayFragment(driveFragment);
    }

    private void displayLoading() {
        LoadingFragment loadingFragment = new LoadingFragment();
        displayFragment(loadingFragment);
    }

}
