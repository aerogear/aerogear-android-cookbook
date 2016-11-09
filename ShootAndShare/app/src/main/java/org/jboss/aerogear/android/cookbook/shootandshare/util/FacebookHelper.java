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
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthorizationConfiguration;
import org.jboss.aerogear.android.authorization.oauth2.OAuth2AuthzModule;
import org.jboss.aerogear.android.authorization.oauth2.OAuthWebViewDialog;
import org.jboss.aerogear.android.cookbook.shootandshare.model.PhotoHolder;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.rest.multipart.MultipartRequestBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

public class FacebookHelper {


    private static final String AUTHZ_ENDPOINT = "www.facebook.com/dialog/oauth";
    private static final String AUTHZ_TOKEN_ENDPOINT = "graph.facebook.com/oauth/access_token";
    private static final String AUTHZ_ACCOOUNT_ID = "facebook-token";
    private static final String AUTHZ_REDIRECT_URL = "https://localhost/";
    private static final String MODULE_NAME = "FacebookOAuth";
    private static final String AUTHZ_CLIENT_ID = "";
    private static final String AUTHZ_CLIENT_SECRET = "";

    static {
        try {

            AuthorizationManager.config(MODULE_NAME, OAuth2AuthorizationConfiguration.class)
                    .setBaseURL(new URL("https://"))
                    .setAuthzEndpoint(AUTHZ_ENDPOINT)
                    .setAccessTokenEndpoint(AUTHZ_TOKEN_ENDPOINT)
                    .setAccountId(AUTHZ_ACCOOUNT_ID)
                    .setClientId(AUTHZ_CLIENT_ID)
                    .setClientSecret(AUTHZ_CLIENT_SECRET)
                    .setRedirectURL(AUTHZ_REDIRECT_URL)
                    .setRefreshEndpoint(AUTHZ_TOKEN_ENDPOINT)
                    .addAdditionalAccessParam(Pair.create("response_type", "code"))
                    .setScopes(Collections.singletonList("publish_actions"))
                    .asModule();

            PipeManager.config("fb-upload", RestfulPipeConfiguration.class).module(AuthorizationManager.getModule(MODULE_NAME))
                    .withUrl(new URL("https://graph.facebook.com/me/photos"))
                    .requestBuilder( new MultipartRequestBuilder())
                    .forClass(PhotoHolder.class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void connect(final Activity activity, final Callback callback) {
        try {
            final OAuth2AuthzModule authzModule = (OAuth2AuthzModule) AuthorizationManager.getModule(MODULE_NAME);

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
            PipeManager.getPipe("fb-upload", activity).save(new PhotoHolder(file), callback);
    }

    public static boolean isConnected() {
        return AuthorizationManager.getModule(MODULE_NAME).isAuthorized();
    }

}
