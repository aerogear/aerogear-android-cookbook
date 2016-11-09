/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.aerogear.android.cookbook.shootandshare.util;

import android.app.Activity;
import android.util.Pair;

import org.jboss.aerogear.android.authorization.AuthorizationManager;
import org.jboss.aerogear.android.authorization.AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.authorization.oauth2.OAuthWebViewDialog;
import org.jboss.aerogear.android.cookbook.shootandshare.model.PhotoHolder;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

public class GooglePlusHelper {

    private static final String AUTHZ_URL = "https://accounts.google.com";
    private static final String AUTHZ_ENDPOINT = "/o/oauth2/auth";
    private static final String AUTHZ_TOKEN_ENDPOINT = "/o/oauth2/token";
    private static final String AUTHZ_ACCOUNT_ID = "google-token";
    private static final String AUTHZ_REDIRECT_URL = "http://localhost";
    private static final String MODULE_NAME = "GoogleDriveAuthz";
    private static final String AUTHZ_CLIENT_ID = "";
    private static final String AUTHZ_CLIENT_SECRET = "";

    static {
        try {
            AuthorizationManager.config(MODULE_NAME, OAuth2AuthorizationConfiguration.class)
                    .setBaseURL(new URL(AUTHZ_URL))
                    .setAuthzEndpoint(AUTHZ_ENDPOINT)
                    .setAccessTokenEndpoint(AUTHZ_TOKEN_ENDPOINT)
                    .setAccountId(AUTHZ_ACCOUNT_ID)
                    .setClientId(AUTHZ_CLIENT_ID)
                    .setClientSecret(AUTHZ_CLIENT_SECRET)
                    .setRedirectURL(AUTHZ_REDIRECT_URL)
                    .setScopes(Collections.singletonList("https://www.googleapis.com/auth/drive"))
                    .addAdditionalAuthorizationParam(Pair.create("access_type", "offline"))
                    .asModule();

            PipeManager.config("gp-upload", RestfulPipeConfiguration.class)
                    .module(AuthorizationManager.getModule(MODULE_NAME))
                    .withUrl(new URL("https://www.googleapis.com/upload/drive/v2/files?uploadType=multipart"))
                    .requestBuilder(new GoogleDriveFileUploadRequestBuilder())
                    .forClass(PhotoHolder.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void connect(final Activity activity, final Callback callback) {
        try {
            final AuthzModule authzModule = AuthorizationManager.getModule(MODULE_NAME);

            authzModule.requestAccess(activity, new Callback<String>() {
                @SuppressWarnings("unchecked")
                @Override
                public void onSuccess(String s) {
                    callback.onSuccess(s);
                }

                @Override
                public void onFailure(Exception e) {
                    if (!e.getMessage().matches(OAuthWebViewDialog.OAuthReceiver.DISMISS_ERROR)) {
                        authzModule.deleteAccount();
                    }
                    callback.onFailure(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings("unchecked")
    public static void upload(final File file, final Callback callback, Activity activity) {
        PipeManager.getPipe("gp-upload", activity).save(new PhotoHolder(file), callback);
    }

    public static boolean isConnected() {
        return AuthorizationManager.getModule(MODULE_NAME).isAuthorized();
    }

}
