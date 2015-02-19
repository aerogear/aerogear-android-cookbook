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
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jboss.aerogear.android.cookbook.passwordmanager.R;
import org.jboss.aerogear.android.cookbook.passwordmanager.model.Credential;

public class DetailFragment extends Fragment {

    private final Credential credential;
    private boolean displayPassword = false;

    public DetailFragment(Credential credential) {
        this.credential = credential;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.detail, null);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(credential.getName());

        TextView username = (TextView) view.findViewById(R.id.username);
        username.setText(credential.getUsername());

        ImageView clipboard = (ImageView) view.findViewById(R.id.clipboard);
        clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClipboard(credential.getPassword());
                Toast.makeText(getActivity(), getString(R.string.copied_clipboard), Toast.LENGTH_SHORT).show();
            }
        });

        final TextView password = (TextView) view.findViewById(R.id.password);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!displayPassword) {
                    password.setText(credential.getPassword());
                } else {
                    password.setText(getString(R.string.password_masked));
                }
                displayPassword = !displayPassword;
            }
        });

        return view;
    }

    private void copyToClipboard(String password) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                    getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(password);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(getString(R.string.password), password);
            clipboard.setPrimaryClip(clip);
        }
    }

}
