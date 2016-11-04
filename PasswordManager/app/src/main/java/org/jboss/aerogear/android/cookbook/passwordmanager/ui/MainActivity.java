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
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.jboss.aerogear.android.cookbook.passwordmanager.PasswordManagerApplication;
import org.jboss.aerogear.android.cookbook.passwordmanager.R;
import org.jboss.aerogear.android.cookbook.passwordmanager.ui.DetailFragment;
import org.jboss.aerogear.android.cookbook.passwordmanager.ui.FormFragment;
import org.jboss.aerogear.android.cookbook.passwordmanager.ui.ListFragment;
import org.jboss.aerogear.android.cookbook.passwordmanager.ui.LoginFragment;
import org.jboss.aerogear.android.cookbook.passwordmanager.model.Credential;
import org.jboss.aerogear.android.security.InvalidKeyException;

public class MainActivity extends AppCompatActivity {

    private PasswordManagerApplication application;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        application = (PasswordManagerApplication) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayLogin();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private void displayFragment(Fragment fragment, boolean enableBack) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (enableBack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction
                .replace(R.id.frame, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    private void displayLogin() {
        displayFragment(new LoginFragment(), false);
    }

    public void displayList() {
        try {
            displayFragment(new ListFragment(), false);
        } catch (InvalidKeyException e) {
            displayLogin();
            Toast.makeText(this, getString(R.string.invalid_credential), Toast.LENGTH_LONG).show();
        }

    }

    public void displayForm() {
        displayFragment(new FormFragment(), true);
    }

    public void displayInfo(Credential credential) {
        Bundle args = new Bundle();
        args.putSerializable(Credential.class.getName(), credential);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        displayFragment(fragment, true);
    }

    public void login(String passphrase) {
        try {
            application.createStore(passphrase);
            displayList();
        } catch (Exception e) {
            displayLogin();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void save(Credential credential) {
        application.getStore().save(credential);
        displayList();
    }

    public void delete(Credential credential) {
        application.getStore().remove(credential.getId());
        displayList();
    }

}
