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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.jboss.aerogear.android.cookbook.passwordmanager.PasswordManagerActivity;
import org.jboss.aerogear.android.cookbook.passwordmanager.R;
import org.jboss.aerogear.android.cookbook.passwordmanager.model.Credential;

public class FormFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.form, container, false);

        final EditText name = (EditText) view.findViewById(R.id.name);
        final EditText username = (EditText) view.findViewById(R.id.username);
        final EditText password = (EditText) view.findViewById(R.id.password);
        final Button save = (Button) view.findViewById(R.id.save);

        save.setOnClickListener(view1 -> {
            if (isEmptyFields(name, username, password)) {
                return;
            }

            Credential credential = new Credential();
            credential.setName(name.getText().toString());
            credential.setUsername(username.getText().toString());
            credential.setPassword(password.getText().toString());

            PasswordManagerActivity activity = (PasswordManagerActivity) getActivity();
            activity.save(credential);
        });

        return view;
    }

    private boolean isEmptyFields(EditText... field) {
        boolean empty = false;

        for (EditText editText : field) {
            if (editText.getText().toString().trim().equals("")) {
                editText.setError(getString(R.string.required));
                empty = true;
            }
        }

        return empty;
    }
}
