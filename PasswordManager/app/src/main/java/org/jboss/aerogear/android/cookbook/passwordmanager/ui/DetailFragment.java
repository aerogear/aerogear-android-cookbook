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
package org.jboss.aerogear.android.cookbook.passwordmanager.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jboss.aerogear.android.cookbook.passwordmanager.R;
import org.jboss.aerogear.android.cookbook.passwordmanager.model.Credential;

public class DetailFragment extends Fragment {

    private boolean displayPassword = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Credential credential =
                (Credential) getArguments().getSerializable(Credential.class.getName());

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.detail, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(credential.getName());

        TextView username = (TextView) view.findViewById(R.id.username);
        username.setText(credential.getUsername());

        final TextView password = (TextView) view.findViewById(R.id.password);
        password.setOnClickListener(view1 -> {
            if (!displayPassword) {
                password.setText(credential.getPassword());
            } else {
                password.setText(getString(R.string.password_masked));
            }
            displayPassword = !displayPassword;
        });

        return view;
    }


}
