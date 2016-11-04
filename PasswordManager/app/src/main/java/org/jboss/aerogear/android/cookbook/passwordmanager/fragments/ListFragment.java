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
package org.jboss.aerogear.android.cookbook.passwordmanager.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jboss.aerogear.android.cookbook.passwordmanager.PasswordManagerActivity;
import org.jboss.aerogear.android.cookbook.passwordmanager.PasswordManagerApplication;
import org.jboss.aerogear.android.cookbook.passwordmanager.R;
import org.jboss.aerogear.android.cookbook.passwordmanager.model.Credential;

import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_list_item_1;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list, container, false);

        PasswordManagerApplication application = (PasswordManagerApplication) getActivity().getApplication();
        List<Credential> credentials = new ArrayList<Credential>(application.getStore().readAll());

        @SuppressWarnings("unchecked")
        ArrayAdapter<Credential> adapter = new ArrayAdapter(getActivity(), simple_list_item_1, credentials);

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view12, position, id) -> {
            Credential credential = (Credential) adapterView.getItemAtPosition(position);

            PasswordManagerActivity activity = (PasswordManagerActivity) getActivity();
            activity.displayInfo(credential);
        });

        listView.setOnItemLongClickListener((adapterView, view1, position, id) -> {
            final Credential credential = (Credential) adapterView.getItemAtPosition(position);

            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.delete))
                    .setMessage(getString(R.string.delete_confirmation))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        PasswordManagerActivity activity = (PasswordManagerActivity) getActivity();
                        activity.delete(credential);
                    })
                    .setNegativeButton(getString(R.string.no), null).show();

            return true;
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> ((PasswordManagerActivity) getActivity()).displayForm());

        return view;
    }

}
