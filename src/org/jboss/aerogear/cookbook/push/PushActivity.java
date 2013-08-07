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
package org.jboss.aerogear.cookbook.push;

import org.jboss.aerogear.android.unifiedpush.MessageHandler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.jboss.aerogear.cookbook.R;

public class PushActivity extends Activity implements MessageHandler{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_layout);
		if (getIntent() != null && getIntent().hasExtra("alert")) {
			onMessage(this, getIntent().getExtras());
		}
                
                ((Button)findViewById(R.id.reg_toggle)).setOnClickListener(registerAction);
                
	}
	
	@Override
	protected void onResume() {
		super.onResume();
                Registrations.registerMainThreadHandler(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Registrations.unregisterMainThreadHandler(this);
	}

	@Override
	public void onDeleteMessage(Context context, Bundle arg0) {
		/*see:https://developer.android.com/reference/com/google/android/gms/gcm/GoogleCloudMessaging.html#MESSAGE_TYPE_DELETED*/
	}

	@Override
	public void onError() {
		/*see: https://developer.android.com/reference/com/google/android/gms/gcm/GoogleCloudMessaging.html#MESSAGE_TYPE_SEND_ERROR*/		
	}

	@Override
	public void onMessage(Context context, Bundle arg0) {
		TextView text = (TextView) findViewById(R.id.hodor);
		text.setText(arg0.getString("alert"));
		text.invalidate();
	}

        private View.OnClickListener registerAction = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainApplication)getApplication()).register();
                ((Button)findViewById(R.id.reg_toggle)).setText("Unregister push");
                ((Button)findViewById(R.id.reg_toggle)).setOnClickListener(unregisterAction);
            }
        };
	
        private View.OnClickListener unregisterAction = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainApplication)getApplication()).unregister();
                ((Button)findViewById(R.id.reg_toggle)).setText("Register push");
                ((Button)findViewById(R.id.reg_toggle)).setOnClickListener(registerAction);
            }
        };
        
}
