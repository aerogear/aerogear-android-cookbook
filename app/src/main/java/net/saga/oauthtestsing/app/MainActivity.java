package net.saga.oauthtestsing.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.impl.authz.AuthzConfig;
import org.jboss.aerogear.android.impl.authz.oauth2.OAuth2AuthzModule;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.Pipe;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static net.saga.oauthtestsing.app.Constants.*;

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
        if(authzModule.isAuthorized()) {
            retriveFiles();
        } else {
            authz();
        }
        return super.onOptionsItemSelected(item);
    }

    private void authz() {
        try {

            AuthzConfig authzConfig = new AuthzConfig(new URL(Constants.AUTHZ_URL), "restMod");
            authzConfig.setAuthzEndpoint(AUTHZ_ENDPOINT);
            authzConfig.setAccessTokenEndpoint(AUTHZ_TOKEN_ENDPOINT);
            authzConfig.setAccountId(AUTHZ_ACCOOUNT_ID);
            authzConfig.setClientId(AUTHZ_CLIENT_ID);
            authzConfig.setClientSecret(AUTHZ_CLIENT_SECRET);
            authzConfig.setRedirectURL(AUTHZ_REDIRECT_URL);
            authzConfig.getAdditionalAuthorizationParams().add(Pair.create("access_type", "offline"));
            authzConfig.setScopes(new ArrayList<String>() {{
                add("https://www.googleapis.com/auth/drive");
            }});

            authzModule = new OAuth2AuthzModule(authzConfig);

            authzModule.requestAccess("state", this, new Callback<String>() {
                @Override
                public void onSuccess(String o) {
                    Log.d("TOKEN ++ ", o);
                    retriveFiles();
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

    private void retriveFiles() {

        displayLoading();

        URL googleDriveURL = null;
        try {
            googleDriveURL = new URL("https://www.googleapis.com/drive/v2");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Pipeline pipeline = new Pipeline(googleDriveURL);

        PipeConfig pipeConfig = new PipeConfig(googleDriveURL, Files.class);
        pipeConfig.setAuthzModule(authzModule);
        pipeConfig.getResponseParser().getMarshallingConfig().setDataRoot("items");
        pipeline.pipe(Files.class, pipeConfig);

        Pipe<Files> documentsPipe = pipeline.get("files", this);
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
