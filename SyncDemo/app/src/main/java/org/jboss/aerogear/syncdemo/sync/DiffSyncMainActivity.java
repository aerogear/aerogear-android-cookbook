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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import org.jboss.aerogear.sync.ClientDocument;
import org.jboss.aerogear.sync.DefaultClientDocument;
import org.jboss.aerogear.syncdemo.R;

public class DiffSyncMainActivity extends SyncActivity {

    public static final String DOCUMENT_ID = "DiffSyncMainActivity.DOCUMENT_ID";
    private ProgressDialog dialog;
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
        
//        documentId = startIntent.getStringExtra(DOCUMENT_ID);
          documentId = "12345";

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
        
        
    }

    @Override
    public void onConnected() {
        clientId = getSyncService().getClientId();
        final ClientDocument<JsonNode> clientDocument = clientDoc(documentId, clientId, JsonUtil.toJsonNode(content));
        Log.i("onConnected", "Seed Document:" + clientDocument);
        getSyncService().addDocument(clientDocument);

        final Button sync = (Button) findViewById(R.id.sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                getSyncService().diffAndSend(clientDoc(documentId, clientId, JsonUtil.toJsonNode(gatherUpdates())));
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

    private static ClientDocument<JsonNode> clientDoc(final String id, final String clientId, final JsonNode content) {        
        return new DefaultClientDocument<>(id, clientId, content);
    }

    @Override
    public void patched(final ClientDocument data) {
        Log.i(DiffSyncMainActivity.class.getName(), "updated:" + data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ClientDocument<JsonNode> document = (ClientDocument<JsonNode>) data;
                final Info updates = JsonUtil.fromJsonNode(document.content());
                setFields(updates);
            }
        });
    }
}
