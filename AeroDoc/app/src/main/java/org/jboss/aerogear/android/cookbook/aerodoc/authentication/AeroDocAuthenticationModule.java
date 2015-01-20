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
package org.jboss.aerogear.android.cookbook.aerodoc.authentication;

import android.os.AsyncTask;
import android.util.Log;

import org.jboss.aerogear.android.authentication.AbstractAuthenticationModule;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.http.HttpProvider;
import org.jboss.aerogear.android.pipe.http.HttpRestProvider;
import org.jboss.aerogear.android.pipe.module.AuthorizationFields;
import org.jboss.aerogear.android.pipe.module.ModuleFields;
import org.jboss.aerogear.android.pipe.util.UrlUtils;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AeroDocAuthenticationModule extends AbstractAuthenticationModule {

    private static final String TAG = AeroDocAuthenticationModule.class.getSimpleName();

    public static final String USERNAME_PARAMETER_NAME = "loginName";
    public static final String PASSWORD_PARAMETER_NAME = "password";

    private final URL baseURL;
    private CookieManager cookieManager;
    private boolean isLoggedIn = false;

    public AeroDocAuthenticationModule(URL baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public URL getBaseURL() {
        return this.baseURL;
    }

    @Override
    public String getLoginEndpoint() {
        return "/rest/login";
    }

    @Override
    public String getLogoutEndpoint() {
        return "/rest/logout";
    }

    @Override
    public String getEnrollEndpoint() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void login(String username, String password, Callback<HeaderAndBody> callback) {
        Map<String, String> loginData = new HashMap<String, String>();
        loginData.put(USERNAME_PARAMETER_NAME, username);
        loginData.put(PASSWORD_PARAMETER_NAME, password);

        this.login(loginData, callback);
    }

    @Override
    public void login(final Map<String, String> loginData, final Callback<HeaderAndBody> callback) {

        AsyncTask<Void, Void, HeaderAndBody> task = new AsyncTask<Void, Void, HeaderAndBody>() {
            private Exception exception = null;

            @Override
            protected HeaderAndBody doInBackground(Void... params) {
                try {
                    URL loginURL = UrlUtils.appendToBaseURL(baseURL, getLoginEndpoint());
                    HttpProvider provider = new HttpRestProvider(loginURL);
                    String loginRequest = new JSONObject(loginData).toString();
                    HeaderAndBody result = provider.post(loginRequest);
                    isLoggedIn = true;
                    return result;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(HeaderAndBody headerAndBody) {
                if (exception == null) {
                    callback.onSuccess(headerAndBody);
                } else {
                    callback.onFailure(exception);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void logout(final Callback<Void> callback) {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            private Exception exception = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL logoutURL = UrlUtils.appendToBaseURL(baseURL, getLogoutEndpoint());
                    HttpProvider provider = new HttpRestProvider(logoutURL);
                    provider.post("");
                    isLoggedIn = false;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    exception = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void ignore) {
                if (exception == null) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(exception);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    @Override
    public ModuleFields loadModule(URI relativeURI, String httpMethod, byte[] requestBody) {
        return new ModuleFields();
    }

    @Override
    public boolean handleError(HttpException e) {
        return false;
    }

}
