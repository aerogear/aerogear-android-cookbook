/**
 * JBoss, Home of Professional Open Source Copyright Red Hat, Inc., and
 * individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jboss.aerogear.syncdemo.sync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.sync.ClientDocument;
import org.jboss.aerogear.sync.DefaultClientDocument;
import org.jboss.aerogear.sync.DiffSyncClient;
import org.jboss.aerogear.sync.DiffSyncClientHandler;
import org.jboss.aerogear.syncdemo.R;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class DiffSyncMainActivity extends ActionBarActivity implements Observer {

    public static final String DOCUMENT_ID = "DiffSyncMainActivity.DOCUMENT_ID";
    private ProgressDialog dialog;
    private DiffSyncClient<String> syncClient;
    private String documentId;
    private String clientId;
    private TextView name;
    private TextView profession;
    private TextView hobby0;
    private TextView hobby1;
    private TextView hobby2;
    private TextView hobby3;
    private Info content;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startIntent = getIntent();
        
        
        setContentView(R.layout.editor);
        clientId = UUID.randomUUID().toString();
        documentId = startIntent.getStringExtra(DOCUMENT_ID);
        name = (TextView) findViewById(R.id.name);
        profession = (TextView) findViewById(R.id.profession);
        hobby0 = (TextView) findViewById(R.id.hobby0);
        hobby1 = (TextView) findViewById(R.id.hobby1);
        hobby2 = (TextView) findViewById(R.id.hobby2);
        hobby3 = (TextView) findViewById(R.id.hobby3);
        content = new Info("Luke Skywalker", "Jedi", "Fighting the Dark Side",
                "going into Tosche Station to pick up some power converters",
                "Kissing his sister",
                "Bulls eyeing Womprats on his T-16");
        setFields(content);

        Log.i("onCreate", "observer :" + this);
        syncClient = DiffSyncClient.<String>forSenderID(getString(R.string.senderID))
                .observer(this)
                .context(getApplicationContext())
                .build();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    syncClient.connect(new Callback<DiffSyncClientHandler>() {

                        @Override
                        public void onSuccess(DiffSyncClientHandler data) {
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    JsonUtil.toJson(content);
                                    final ClientDocument<String> clientDocument = clientDoc(documentId, clientId, JsonUtil.toJson(content));
                                    Log.i("onCreate", "Seed Document:" + clientDocument);
                                    syncClient.addDocument(clientDocument);
                                }
                            }).start();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("FAILURE", e.getMessage(), e);
                            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }
                    });
                } catch (InterruptedException ex) {
                    Log.e("TAG", ex.getMessage(), ex);
                }

                return null;
            }
        }.execute();

        final Button sync = (Button) findViewById(R.id.sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialog = ProgressDialog.show(DiffSyncMainActivity.this, getString(R.string.wait), getString(R.string.syncing), true, false);
                new AsyncTask<ClientDocument, Void, String>() {
                    @Override
                    protected String doInBackground(final ClientDocument... params) {
                        Log.i("doInBackground", "Document:" + params[0]);
                        syncClient.diffAndSend(params[0]);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(final String s) {
                        dialog.dismiss();
                    }
                }.execute(clientDoc(documentId, clientId, JsonUtil.toJson(gatherUpdates())));
            }

        });
    }

    private Info gatherUpdates() {
        return new Info(content.getName().toString(),
                profession.getText().toString(),
                hobby0.getText().toString(),
                hobby1.getText().toString(),
                hobby2.getText().toString(),
                hobby3.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setFields(final Info content) {
        name.setText(content.getName());
        profession.setText(content.getProfession());
        hobby0.setText(content.getHobbies().get(0));
        hobby1.setText(content.getHobbies().get(1));
        hobby2.setText(content.getHobbies().get(2));
        hobby3.setText(content.getHobbies().get(3));
    }

    private static ClientDocument<String> clientDoc(final String id, final String clientId, final String content) {        
        return new DefaultClientDocument<>(id, clientId, content);
    }

    @Override
    public void update(final Observable observable, final Object data) {
        Log.i(DiffSyncMainActivity.class.getName(), "updated:" + data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ClientDocument<String> document = (ClientDocument<String>) data;
                final Info updates = JsonUtil.fromJson(document.content());
                setFields(updates);
            }
        });
    }
}
