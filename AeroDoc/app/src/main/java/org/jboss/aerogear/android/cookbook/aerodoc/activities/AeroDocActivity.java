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
package org.jboss.aerogear.android.cookbook.aerodoc.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.jboss.aerogear.android.cookbook.aerodoc.AeroDocApplication;
import org.jboss.aerogear.android.cookbook.aerodoc.R;
import org.jboss.aerogear.android.cookbook.aerodoc.fragments.AeroDocLeadsAcceptedFragments;
import org.jboss.aerogear.android.cookbook.aerodoc.fragments.AeroDocLeadsAvailableFragments;
import org.jboss.aerogear.android.cookbook.aerodoc.fragments.AeroDocLoginFragment;
import org.jboss.aerogear.android.cookbook.aerodoc.handler.NotifyingMessageHandler;
import org.jboss.aerogear.android.cookbook.aerodoc.model.MessageType;
import org.jboss.aerogear.android.cookbook.aerodoc.model.SaleAgent;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;

import java.nio.charset.Charset;

public class AeroDocActivity extends AppCompatActivity implements MessageHandler {

    private static final int LOCATION_REQUEST = 0x100;

    private enum Display {
        LOGIN, AVAILABLE_LEADS, LEADS_ACCEPTED
    }

    private static final String TAG = AeroDocActivity.class.getSimpleName();

    private AeroDocApplication application;
    private Display display;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        application = (AeroDocApplication) getApplication();
        setContentView(R.layout.main);

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION },
                LOCATION_REQUEST);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if (application.isLoggedIn()) {
                displayAvailableLeadsScreen();
            } else {
                displayLoginScreen();
            }
        } else {
            requestPermissions();
        }

        RegistrarManager.unregisterBackgroundThreadHandler(NotifyingMessageHandler.instance);
        RegistrarManager.registerMainThreadHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RegistrarManager.unregisterMainThreadHandler(this);
        RegistrarManager.registerBackgroundThreadHandler(NotifyingMessageHandler.instance);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.leads, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leads_accepted:
                displayLeadsAcceptedScreen();
                break;
            case R.id.available_leads:
                displayAvailableLeadsScreen();
                break;
            case R.id.refresh:
                updateLeads();
                break;
            case R.id.logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.menuAvailableLead, Display.AVAILABLE_LEADS.equals(display));
        menu.setGroupVisible(R.id.menuLeadsAccepted, Display.LEADS_ACCEPTED.equals(display));
        menu.setGroupVisible(R.id.menuLogout, !Display.LOGIN.equals(display));
        return super.onPrepareOptionsMenu(menu);
    }

    public void onMessage(Context context, Bundle bundle) {
        String messageType = bundle.getString("messageType");
        if (MessageType.PUSHED.getType().equals(messageType)) {
            updateLeads();
            Toast.makeText(this, bundle.getString("alert"), Toast.LENGTH_SHORT).show();
        } else if (MessageType.ACCPET.getType().equals(messageType)) {
            updateLeads();
        }
    }

    private void displayLoginScreen() {
        displayFragment(Display.LOGIN, new AeroDocLoginFragment());
    }

    private void displayAvailableLeadsScreen() {
        displayFragment(Display.AVAILABLE_LEADS, new AeroDocLeadsAvailableFragments());
    }

    private void displayLeadsAcceptedScreen() {
        displayFragment(Display.LEADS_ACCEPTED, new AeroDocLeadsAcceptedFragments());
    }

    private void displayFragment(Display display, Fragment fragment) {
        this.display = display;
        this.supportInvalidateOptionsMenu();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }

    public void login(final String user, final String pass) {
        showProgressDialog(getString(R.string.loging));
        application.login(user, pass, new Callback<HeaderAndBody>() {
            @Override
            public void onSuccess(HeaderAndBody headerAndBody) {
                AeroDocActivity activity = AeroDocActivity.this;
                String response = new String(headerAndBody.getBody(), Charset.forName("UTF-8"));
                SaleAgent saleAgent = new Gson().fromJson(response, SaleAgent.class);
                ((AeroDocApplication) activity.getApplication()).setSaleAgent(saleAgent);
                ((AeroDocApplication) activity.getApplication()).registerDeviceOnPushServer(user);
                activity.dismissDialog();
                activity.displayAvailableLeadsScreen();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, e.getMessage(), e);
                AeroDocActivity activity = AeroDocActivity.this;
                activity.dismissDialog();
                activity.displayErrorMessage(e);
            }
        });
    }

    public void logout() {
        showProgressDialog(getString(R.string.logout));
        application.logout(new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AeroDocActivity activity = AeroDocActivity.this;
                activity.displayLoginScreen();
                activity.dismissDialog();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, e.getMessage(), e);
                AeroDocActivity activity = AeroDocActivity.this;
                activity.displayErrorMessage(e);
                activity.dismissDialog();
            }
        });
    }

    private void updateLeads() {
        AeroDocLeadsAvailableFragments leadsFragments = (AeroDocLeadsAvailableFragments)
                getFragmentManager().findFragmentById(R.id.frame);
        leadsFragments.retrieveLeads();
    }

    public void showProgressDialog(final String message) {
        dialog = ProgressDialog.show(AeroDocActivity.this, getString(R.string.wait), message, true, true);
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void displayErrorMessage(Exception e) {
        Log.e(TAG, e.getMessage(), e);
//        Toast.makeText(AeroDocActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        Toast.makeText(AeroDocActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(getApplicationContext(), AeroDocActivity.class));
                    finish();
                } else {

                    finish();
                }
                return;
            }

        }
    }


}
