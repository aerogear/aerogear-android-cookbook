/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.android.cookbook.aerodoc.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.jboss.aerogear.android.cookbook.aerodoc.AeroDocApplication;
import org.jboss.aerogear.android.cookbook.aerodoc.R;
import org.jboss.aerogear.android.cookbook.aerodoc.handler.NotifyingMessageHandler;
import org.jboss.aerogear.android.cookbook.aerodoc.model.MessageType;
import org.jboss.aerogear.android.cookbook.aerodoc.model.SaleAgent;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.Pipe;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements MessageHandler,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private AeroDocApplication application;

    private View contentPanel;

    private AvailableLeadsFragments availableLeadsFragments;
    private AcceptedLeadsFragments acceptedLeadsFragments;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    // -- Android Life Cycle ----------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (AeroDocApplication) getApplication();

        contentPanel = findViewById(R.id.contentPanel);

        availableLeadsFragments = new AvailableLeadsFragments();
        acceptedLeadsFragments = new AcceptedLeadsFragments();

        // -- Dropdown (Status)

        Spinner spinner = (Spinner) findViewById(R.id.status);

        ArrayAdapter<String> spinnerAdapter = (ArrayAdapter) spinner.getAdapter();
        int actualStatus = spinnerAdapter.getPosition(application.getSaleAgent().getStatus());
        spinner.setSelection(actualStatus, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int position, long id) {
                String status = (String) adapterView.getItemAtPosition(position);
                if (!application.getSaleAgent().getStatus().equals(status)) {
                    updateStatus(status);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // -- Tabs (Leads - Available & Archived)

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);

        pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);

        // -- Track Moviment

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connect to Google services
        mGoogleApiClient.connect();

        RegistrarManager.unregisterBackgroundThreadHandler(NotifyingMessageHandler.instance);
        RegistrarManager.registerMainThreadHandler(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect from Google services
        mGoogleApiClient.disconnect();

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
        if (item.getItemId() == R.id.logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    // -- MessageHandler --------------------------------------------------------------------------

    @Override
    public void onMessage(Context context, Bundle bundle) {
        /*
          MessageType.PUSHED when new lead is available and need to be displayed
          MessageType.ACCPET when a lead was accepeted and need to be removed from the list.
        */
        String messageType = bundle.getString("messageType");
        if (MessageType.PUSHED.getType().equals(messageType)) {
            Toast.makeText(this, bundle.getString("alert"), Toast.LENGTH_SHORT).show();
        } else if (MessageType.ACCPET.getType().equals(messageType)) {
            acceptedLeadsFragments.retrieveLeads();
        }
        availableLeadsFragments.retrieveLeads();
    }

    // -- GoogleApiClient.ConnectionCallbacks -----------------------------------------------------

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MainActivityPermissionsDispatcher.trackMovimentWithCheck(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Stop request track moviment
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    // GoogleApiClient.OnConnectionFailedListener -------------------------------------------------

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // -- LocationListener ------------------------------------------------------------------------

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    // -- ViewPagerAdapter ------------------------------------------------------------------------

    class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (position == 0) ? getString(R.string.available) : getString(R.string.accepted);
        }

        @Override
        public Fragment getItem(int position) {
            return (position == 0) ? availableLeadsFragments : acceptedLeadsFragments;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

    public void logout() {

        final MaterialDialog dialog = showProgressDialog(R.string.logout);

        application.logout(new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                dialog.dismiss();
                Log.e(TAG, e.getMessage(), e);
                showSnackBar(e.getMessage());
            }
        });

    }

    // -- Change Sale -----------------------------------------------------------------------------

    private void updateStatus(String status) {

        final MaterialDialog dialog = showProgressDialog(R.string.updating_status);

        SaleAgent saleAgent = application.getSaleAgent();
        saleAgent.setStatus(status);

        Pipe pipe = application.getSaleAgentPipe(this);
        pipe.save(saleAgent, new Callback<Object>() {
            @Override
            public void onSuccess(Object data) {
                dialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                dialog.dismiss();
                Log.e(TAG, e.getMessage(), e);
                showSnackBar(e.getMessage());
            }
        });
    }

    // -- Dialog Helpers --------------------------------------------------------------------------

    private MaterialDialog showProgressDialog(@StringRes int message) {
        return new MaterialDialog.Builder(this)
                .title(R.string.wait)
                .content(message)
                .progress(true, 0)
                .show();
    }

    private void showSnackBar(String message) {
        Snackbar.make(contentPanel, message, Snackbar.LENGTH_LONG).show();
    }

    // -- Track Moviment

    @SuppressWarnings("MissingPermission")
    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION})
    void trackMoviment() {
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION})
    void showRationale(final PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION})
    void showDenied() {
        Snackbar.make(contentPanel, R.string.location_denied, Snackbar.LENGTH_LONG)
                .setAction(R.string.ask_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivityPermissionsDispatcher.trackMovimentWithCheck(MainActivity.this);
                    }
                })
                .show();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION})
    void showNeverAskAgain() {
        Toast.makeText(this, R.string.request_location_never_ask_again, Toast.LENGTH_LONG).show();
    }

    private void updateLocation(Location location) {

        SaleAgent saleAgent = application.getSaleAgent();

        saleAgent.setLongitude(location.getLongitude());
        saleAgent.setLatitude(location.getLatitude());

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            saleAgent.setLocation(addresses.get(0).getLocality());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
        }

        Pipe pipe = application.getSaleAgentPipe(this);
        pipe.save(saleAgent, new Callback<List<SaleAgent>>() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "could not save sales agent", e);
            }

            @Override
            public void onSuccess(List<SaleAgent> data) {
                Log.d(TAG, "Sale Agent location success updated");
            }
        });
    }

}
