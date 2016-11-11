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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.jboss.aerogear.android.cookbook.aerodoc.AeroDocApplication;
import org.jboss.aerogear.android.cookbook.aerodoc.R;
import org.jboss.aerogear.android.cookbook.aerodoc.handler.NotifyingMessageHandler;
import org.jboss.aerogear.android.cookbook.aerodoc.model.MessageType;
import org.jboss.aerogear.android.cookbook.aerodoc.model.SaleAgent;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.Pipe;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;

public class MainActivity extends AppCompatActivity implements MessageHandler {

    private static final int LOCATION_REQUEST = 0x100;
    private static final String TAG = MainActivity.class.getSimpleName();

    private AeroDocApplication application;

    private View contentPanel;

    private AvailableLeadsFragments availableLeadsFragments;
    private AcceptedLeadsFragments acceptedLeadsFragments;

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
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        if (item.getItemId() == R.id.logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    // -- MessageHandler --------------------------------------------------------------------------

    @Override
    public void onMessage(Context context, Bundle bundle) {
        /**
         * MessageType.PUSHED when new lead is available and need to be displayed
         * MessageType.ACCPET when a lead was accepeted and need to be removed from the list.
         */
        String messageType = bundle.getString("messageType");
        if (MessageType.PUSHED.getType().equals(messageType)) {
            Toast.makeText(this, bundle.getString("alert"), Toast.LENGTH_SHORT).show();
        } else if (MessageType.ACCPET.getType().equals(messageType)) {
            acceptedLeadsFragments.retrieveLeads();
        }
        availableLeadsFragments.retrieveLeads();
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

}
