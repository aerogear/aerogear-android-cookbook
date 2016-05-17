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
package org.jboss.aerogear.android.cookbook.aerodoc;

import android.app.Application;
import android.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import org.jboss.aerogear.android.authentication.AuthenticationManager;
import org.jboss.aerogear.android.authentication.AuthenticationModule;
import org.jboss.aerogear.android.cookbook.aerodoc.activities.AeroDocActivity;
import org.jboss.aerogear.android.cookbook.aerodoc.authentication.AeroDocAuthenticationConfiguration;
import org.jboss.aerogear.android.cookbook.aerodoc.authentication.AeroDocAuthenticationConfigurationProvider;
import org.jboss.aerogear.android.cookbook.aerodoc.model.Lead;
import org.jboss.aerogear.android.cookbook.aerodoc.model.SaleAgent;
import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.pipe.Pipe;
import org.jboss.aerogear.android.pipe.PipeManager;
import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.rest.RestfulPipeConfiguration;
import org.jboss.aerogear.android.pipe.util.UrlUtils;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;
import org.jboss.aerogear.android.unifiedpush.gcm.AeroGearGCMPushConfiguration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class AeroDocApplication extends Application {

    private static final String BASE_BACKEND_URL = "";

    private static final String UNIFIED_PUSH_URL = "";
    private static final String GCM_SENDER_ID = "";
    private static final String VARIANT_ID = "";
    private static final String SECRET = "";

    private AuthenticationModule authenticationModule;
    private SQLStore<Lead> localStore;
    private SaleAgent saleAgent;

    public SaleAgent getSaleAgent() {
        return saleAgent;
    }

    public void setSaleAgent(SaleAgent saleAgent) {
        this.saleAgent = saleAgent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        configureBackendAuthentication();
        createApplicationPipes();
        createLocalStorage();
    }

    private void configureBackendAuthentication() {

        try {

            final URL serverURL = new URL(BASE_BACKEND_URL);

            AuthenticationManager
                    .registerConfigurationProvider(AeroDocAuthenticationConfiguration.class,
                            new AeroDocAuthenticationConfigurationProvider());

            authenticationModule = AuthenticationManager
                    .config("AeroDocAuth", AeroDocAuthenticationConfiguration.class)
                    .baseURL(serverURL)
                    .asModule();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    private void createApplicationPipes() {

        try {

            final URL serverURL = new URL(BASE_BACKEND_URL);

            PipeManager.config("lead", RestfulPipeConfiguration.class)
                    .withUrl(UrlUtils.appendToBaseURL(serverURL, "/rest/leads"))
                    .module(authenticationModule)
                    .forClass(Lead.class);

            PipeManager.config("agent", RestfulPipeConfiguration.class)
                    .withUrl(UrlUtils.appendToBaseURL(serverURL, "/rest/saleagents"))
                    .module(authenticationModule)
                    .forClass(SaleAgent.class);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    private void createLocalStorage() {

        DataManager.config("lead", SQLStoreConfiguration.class)
                .withContext(getApplicationContext())
                .store(Lead.class);

        localStore = (SQLStore) DataManager.getStore("lead");
        localStore.openSync();

    }

    public void registerDeviceOnPushServer(String alias) {

        try {

            RegistrarManager.config("AeroDoc", AeroGearGCMPushConfiguration.class)
                    .setPushServerURI(new URI(UNIFIED_PUSH_URL))
                    .setSenderId(GCM_SENDER_ID)
                    .setVariantID(VARIANT_ID)
                    .setSecret(SECRET)
                    .setAlias(alias)
                    .setCategories(Arrays.asList("lead"))
                    .asRegistrar();

            PushRegistrar registrar = RegistrarManager.getRegistrar("AeroDoc");
            registrar.register(getApplicationContext(), new Callback<Void>() {
                @Override
                public void onSuccess(Void data) {
                    Log.d("GCM", "Registered");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public void login(String username, String password, Callback<HeaderAndBody> callback) {
        authenticationModule.login(username, password, callback);
    }

    public void logout(Callback<Void> callback) {
        authenticationModule.logout(callback);
    }

    public boolean isLoggedIn() {
        return authenticationModule.isLoggedIn();
    }

    public Pipe<Lead> getLeadPipe(Fragment fragment) {
        return PipeManager.getPipe("lead", fragment, getApplicationContext());
    }

    public Pipe<SaleAgent> getSaleAgentPipe(Fragment fragment) {
        return PipeManager.getPipe("agent", fragment, getApplicationContext());
    }

    public SQLStore<Lead> getLocalStore() {
        return localStore;
    }

}