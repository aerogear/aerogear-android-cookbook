package net.saga.oauthtestsing.app;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.Pipeline;
import org.jboss.aerogear.android.authorization.AuthzConfig;
import org.jboss.aerogear.android.impl.authz.AGRestAuthzModule;
import org.jboss.aerogear.android.impl.pipeline.PipeConfig;
import org.jboss.aerogear.android.pipeline.Pipe;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity {

    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    Log.d("TOKEN ++ ", o);


                    URL readGoogleDriveURL = null;
                    try {
                        readGoogleDriveURL = new URL("https://www.googleapis.com/drive/v2");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Pipeline pipeline = new Pipeline(readGoogleDriveURL);

                    PipeConfig docConfig = new PipeConfig(readGoogleDriveURL, Files.class);
                    docConfig.setAuthzModule(module);
                    docConfig.getResponseParser().getMarshallingConfig().setDataRoot("items");
                    pipeline.pipe(Files.class, docConfig);
                    Pipe<Files> documentsPipe = pipeline.get("files", MainActivity.this);
                    documentsPipe.read(new Callback<List<Files>>() {
                        @Override
                        public void onSuccess(final List<Files> fileses) {
                            Toast.makeText(getApplicationContext(), fileses.size() + " files fetched", Toast.LENGTH_LONG).show();
                            setListAdapter(new ArrayAdapter<Files>(getApplicationContext(), android.R.layout.simple_list_item_1, fileses){
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    if (convertView == null) {
                                        convertView = getLayoutInflater().inflate(R.layout.drive_list_item, null);
                                    }

                                    ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                                    new DownloadImageTask(imageView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fileses.get(position).getIconLink());

                                    TextView name = (TextView) convertView.findViewById(R.id.textView);
                                    name.setText(fileses.get(position).getTitle());

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


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private static class OAuthReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getDataString(), Toast.LENGTH_LONG).show();
        }
    }


}
