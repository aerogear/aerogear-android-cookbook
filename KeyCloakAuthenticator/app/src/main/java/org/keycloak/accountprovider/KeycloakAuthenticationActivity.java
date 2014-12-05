/**
 * JBoss,HomeofProfessionalOpenSource
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
package org.keycloak.accountprovider;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import org.keycloak.accountprovider.token.AccessTokenExchangeLoader;
import org.keycloak.accountprovider.util.IOUtils;


public class KeycloakAuthenticationActivity extends AccountAuthenticatorActivity implements LoaderManager.LoaderCallbacks<KeycloakAccount> {

    private Keycloak kc;
    private static final String ACCESS_TOKEN_KEY = "accessToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        kc = new Keycloak(this);
        Account[] accounts = AccountManager.get(this).getAccountsByType(Keycloak.ACCOUNT_TYPE);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public Loader<KeycloakAccount> onCreateLoader(int i, Bundle bundle) {
        return new AccessTokenExchangeLoader(this, bundle.getString(ACCESS_TOKEN_KEY));
    }

    @Override
    public void onLoadFinished(Loader<KeycloakAccount> keyCloakAccountLoader, KeycloakAccount keycloakAccount) {
        AccountAuthenticatorResponse response = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        String keyCloakAccountJson = new Gson().toJson(keycloakAccount);
        Bundle accountBundle = new Bundle();
        accountBundle.putString(Keycloak.ACCOUNT_KEY, keyCloakAccountJson);


        AccountManager am = AccountManager.get(this);
        Account androidAccount = new Account(keycloakAccount.getPreferredUsername(), Keycloak.ACCOUNT_TYPE);
        Account[] accounts = am.getAccountsByType(Keycloak.ACCOUNT_TYPE);
        for (Account existingAccount : accounts) {
            if (existingAccount.name == androidAccount.name) {
                am.setUserData(androidAccount, Keycloak.ACCOUNT_KEY, keyCloakAccountJson);
                if (response != null) {
                    response.onResult(accountBundle);
                }
                finish();
            }
        }

        am.removeAccount(androidAccount, null, null);
        am.addAccountExplicitly(androidAccount, null, accountBundle);

        if (response != null) {
            response.onResult(accountBundle);
        }
        finish();


    }

    @Override
    public void onLoaderReset(Loader<KeycloakAccount> keyCloakAccountLoader) {

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private Keycloak kc;
        private WebView webView;

        public PlaceholderFragment() {

        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if (kc == null) {
                kc = new Keycloak(activity);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.keycloak_login_fragment, container, false);
            webView = (WebView) rootView.findViewById(R.id.webview);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    if (url.contains("code=")) {
                        final String token = IOUtils.fetchToken(url);
                        Log.d("TOKEN", token);

                        Bundle data = new Bundle();
                        data.putString(ACCESS_TOKEN_KEY, token);
                        getLoaderManager().initLoader(1, data, (LoaderManager.LoaderCallbacks) (getActivity())).forceLoad();

                        return true;
                    }

                    return false;
                }


            });
            webView.loadUrl(kc.createLoginUrl());
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
        }
    }
}
