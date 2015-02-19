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
package org.jboss.aerogear.android.cookbook.passwordmanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jboss.aerogear.android.cookbook.passwordmanager.fragments.DetailFragment;
import org.jboss.aerogear.android.cookbook.passwordmanager.fragments.FormFragment;
import org.jboss.aerogear.android.cookbook.passwordmanager.fragments.ListFragment;
import org.jboss.aerogear.android.cookbook.passwordmanager.fragments.LoginFragment;
import org.jboss.aerogear.android.cookbook.passwordmanager.model.Credential;
import org.jboss.aerogear.android.security.InvalidKeyException;

import java.security.spec.InvalidKeySpecException;
import java.util.Collection;

public class PasswordManagerActivity extends Activity {

    private enum Display {
        LOGIN, LIST, FORM, DETAIL
    }

    private PasswordManagerApplication application;
    private Display currentFragment;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crypto_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.group_add, Display.LIST.equals(currentFragment));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        displayForm();
        return super.onOptionsItemSelected(item);
    }

    private void displayFragment(Fragment fragment, Display display, boolean enableBack) {
        currentFragment = display;

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (enableBack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction
                .replace(R.id.frame, fragment, fragment.getClass().getSimpleName())
                .commit();

        invalidateOptionsMenu();
    }

    private void displayLogin() {
        displayFragment(new LoginFragment(), Display.LOGIN, false);
    }

    public void displayList() {
        try {
            Collection credentials = application.getStore().readAll();
            displayFragment(new ListFragment(credentials), Display.LIST, false);
        } catch (InvalidKeyException e) {
            displayLogin();
            Toast.makeText(this, getString(R.string.invalid_credential), Toast.LENGTH_LONG).show();
        }

    }

    private void displayForm() {
        displayFragment(new FormFragment(), Display.FORM, true);
    }

    public void displayInfo(Credential credential) {
        displayFragment(new DetailFragment(credential), Display.DETAIL, true);
    }

    public void login(String passphrase) {
        try {
            application.createStore(passphrase);
            displayList();
        } catch (InvalidKeySpecException e) {
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
