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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jboss.aerogear.android.cookbook.aerodoc.AeroDocApplication;
import org.jboss.aerogear.android.cookbook.aerodoc.R;
import org.jboss.aerogear.android.cookbook.aerodoc.model.Lead;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.Pipe;

import java.util.List;

import static android.R.layout.simple_list_item_1;

public class AvailableLeadsFragments extends Fragment {

    private static final String TAG = AvailableLeadsFragments.class.getName();

    private View view;

    private AeroDocApplication application;
    private MainActivity activity;
    private ListView listView;
    private ArrayAdapter<Lead> adapter;
    private List<Lead> leads;

    // -- Android Life Cycle ----------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        application = (AeroDocApplication) getActivity().getApplication();
        activity = (MainActivity) getActivity();

        view = inflater.inflate(R.layout.fragment_available_leads, container, false);

        listView = (ListView) view.findViewById(R.id.leads);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Lead lead = (Lead) adapterView.getItemAtPosition(position);
                displayLead(lead);
            }
        });

        retrieveLeads();
//        trackMovement();

        return view;
    }

    // Leads --------------------------------------------------------------------------------------

    public void retrieveLeads() {

        Pipe<Lead> pipe = application.getLeadPipe(this);
        pipe.read(new Callback<List<Lead>>() {
            @Override
            public void onSuccess(List<Lead> data) {
                AvailableLeadsFragments.this.leads = data;
                adapter = new ArrayAdapter<Lead>(activity, simple_list_item_1, leads);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, e.getMessage(), e);
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void displayLead(final Lead lead) {

        new MaterialDialog.Builder(getContext())
                .title(lead.getName())
                .positiveText(R.string.accept)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        acceptLead(lead);
                    }
                })
                .negativeText(R.string.dismiss)
                .show();

    }

    private void acceptLead(final Lead lead) {

        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(R.string.wait)
                .content(R.string.updating_lead)
                .progress(true, 0)
                .show();

        lead.setSaleAgent(application.getSaleAgent().getId());
        Pipe leadPipe = application.getLeadPipe(this);
        leadPipe.save(lead, new Callback() {
            @Override
            public void onSuccess(Object data) {
                application.getLocalStore().save(lead);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Exception e) {
                dialog.dismiss();
                Log.e(TAG, e.getMessage(), e);
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

}
