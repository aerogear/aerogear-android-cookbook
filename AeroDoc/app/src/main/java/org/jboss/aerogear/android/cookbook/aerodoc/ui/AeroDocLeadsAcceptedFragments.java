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
package org.jboss.aerogear.android.cookbook.aerodoc.ui;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.jboss.aerogear.android.cookbook.aerodoc.AeroDocApplication;
import org.jboss.aerogear.android.cookbook.aerodoc.R;
import org.jboss.aerogear.android.cookbook.aerodoc.ui.AeroDocActivity;
import org.jboss.aerogear.android.cookbook.aerodoc.model.Lead;

import java.util.ArrayList;
import java.util.Collection;

import static android.R.layout.simple_list_item_1;

public class AeroDocLeadsAcceptedFragments extends Fragment {

    private AeroDocApplication application;
    private AeroDocActivity activity;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        application = (AeroDocApplication) getActivity().getApplication();
        activity = (AeroDocActivity) getActivity();

        final View view = inflater.inflate(R.layout.leads_accepted, null);

        listView = (ListView) view.findViewById(R.id.leads);

        retrieveLeads();

        return view;
    }

    public void retrieveLeads() {
        Collection<Lead> leads = application.getLocalStore().readAll();

        ArrayAdapter<Lead> adapter = new ArrayAdapter<Lead>(activity, simple_list_item_1, new ArrayList<Lead>(leads));
        listView.setAdapter(adapter);
    }
}
