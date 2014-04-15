package net.saga.oauthtestsing.app;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.authorization.AuthzConfig;
import org.jboss.aerogear.android.impl.authz.oauth2.AGOAuth2AuthzModule;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.Pipe;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static net.saga.oauthtestsing.app.Constants.*;

public class MainActivity extends ListActivity {

    private AGOAuth2AuthzModule authzModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.authz();
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

            authzModule = new AGOAuth2AuthzModule(authzConfig);

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

                Toast.makeText(getApplicationContext(), fileses.size() + " files fetched", Toast.LENGTH_LONG).show();

                setListAdapter(new ArrayAdapter<Files>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, fileses) {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        Files files = fileses.get(position);

                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(R.layout.drive_list_item, null);
                        }

                        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                        Picasso.with(getApplicationContext()).load(files.getIconLink()).into(imageView);

                        TextView name = (TextView) convertView.findViewById(R.id.textView);
                        name.setText(files.getTitle());

                        return convertView;
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("EXCEPTION", e.getMessage(), e);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
