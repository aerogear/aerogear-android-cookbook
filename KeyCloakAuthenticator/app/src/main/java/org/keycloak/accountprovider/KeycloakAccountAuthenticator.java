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

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import org.jboss.aerogear.android.http.HeaderAndBody;
import org.jboss.aerogear.android.http.HttpException;
import org.jboss.aerogear.android.impl.http.HttpRestProvider;
import org.keycloak.accountprovider.util.TokenExchangeUtils;

import java.util.Date;


/**
 * Created by Summers on 9/12/2014.
 */
public class KeycloakAccountAuthenticator extends AbstractAccountAuthenticator {

    private final Context context;
    private final AccountManager am;
    private final Keycloak kc;
    public KeycloakAccountAuthenticator(Context context) {
        super(context);
        this.context = context.getApplicationContext();
        this.am = AccountManager.get(context);
        this.kc = new Keycloak(context);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Bundle toReturn = new Bundle();
        AccountManager am = AccountManager.get(context);

        if (options == null || options.getString(Keycloak.ACCOUNT_KEY) == null) {
            toReturn.putParcelable(AccountManager.KEY_INTENT, new Intent(context, KeycloakAuthenticationActivity.class).putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response));
            toReturn.putParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        } else {
            KeycloakAccount account = new Gson().fromJson(options.getString(Keycloak.ACCOUNT_KEY), KeycloakAccount.class);
            am.removeAccount(new Account(account.getPreferredUsername(), Keycloak.ACCOUNT_TYPE), null, null);
            am.addAccountExplicitly(new Account(account.getPreferredUsername(), Keycloak.ACCOUNT_TYPE), null, options);
            toReturn.putString(AccountManager.KEY_ACCOUNT_NAME, account.getPreferredUsername());
            toReturn.putString(AccountManager.KEY_ACCOUNT_TYPE, Keycloak.ACCOUNT_TYPE);

        }

        return toReturn;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {


        Account[] accounts = am.getAccountsByType(Keycloak.ACCOUNT_TYPE);
        for (Account existingAccount : accounts) {
            if (existingAccount.name == account.name) {
               break;
            }
        }


        String keyCloackAccount = am.getUserData(account, Keycloak.ACCOUNT_KEY);
        KeycloakAccount kcAccount = new Gson().fromJson(keyCloackAccount, KeycloakAccount.class);
        if (kcAccount == null) {
            Bundle toReturn = new Bundle();
            toReturn.putParcelable(AccountManager.KEY_INTENT, new Intent(context, KeycloakAuthenticationActivity.class).putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse));
            toReturn.putParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);
            return toReturn;

        }
        if (new Date(kcAccount.getExpiresOn()).before(new Date())) {
            try {
                TokenExchangeUtils.refreshToken(kcAccount, kc);
                String accountJson = new Gson().toJson(kcAccount);
                am.setUserData(new Account(kcAccount.getPreferredUsername(), Keycloak.ACCOUNT_TYPE), Keycloak.ACCOUNT_KEY, accountJson);
            } catch (HttpException e) {
                if (e.getStatusCode() / 100 == 4) {
                    Bundle toReturn = new Bundle();
                    toReturn.putParcelable(AccountManager.KEY_INTENT, new Intent(context, KeycloakAuthenticationActivity.class).putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse));
                    toReturn.putParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);
                    return toReturn;
                } else {
                    Bundle toReturn = new Bundle();
                    toReturn.putString(AccountManager.KEY_ERROR_CODE, e.getStatusCode() + "");
                    toReturn.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
                    return toReturn;
                }
            }
        }

        Bundle toReturn = new Bundle();
        toReturn.putString(AccountManager.KEY_AUTHTOKEN, kcAccount.getAccessToken());
        toReturn.putString(AccountManager.KEY_ACCOUNT_NAME, kcAccount.getPreferredUsername());
        toReturn.putString(AccountManager.KEY_ACCOUNT_TYPE, Keycloak.ACCOUNT_TYPE);
        return toReturn;
    }


    @Override
    public String getAuthTokenLabel(String s) {
        return "KeyCloak Token";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {

        String keyCloackAccount = AccountManager.get(context).getUserData(account, Keycloak.ACCOUNT_KEY);
        KeycloakAccount kca = new Gson().fromJson(keyCloackAccount, KeycloakAccount.class);

        if (kca.getExpiresOn() < new Date().getTime()) {
            throw new RuntimeException("token expired");
        }

        Bundle toReturn = new Bundle();
        toReturn.putString(AccountManager.KEY_ACCOUNT_NAME, kca.getPreferredUsername());
        toReturn.putString(AccountManager.KEY_ACCOUNT_TYPE, Keycloak.ACCOUNT_TYPE);

        return toReturn;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }



}
