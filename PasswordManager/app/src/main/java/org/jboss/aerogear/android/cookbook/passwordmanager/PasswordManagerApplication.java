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

import android.app.Application;

import org.jboss.aerogear.android.cookbook.passwordmanager.model.Credential;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.sql.EncryptedSQLStore;
import org.jboss.aerogear.android.store.sql.EncryptedSQLStoreConfiguration;

import java.security.spec.InvalidKeySpecException;

public class PasswordManagerApplication extends Application {

    private EncryptedSQLStore<Credential> store;

    public EncryptedSQLStore<Credential> getStore() {
        return store;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void createStore(String passphrase) throws InvalidKeySpecException {
        store = (EncryptedSQLStore<Credential>) DataManager.config("pwdStore", EncryptedSQLStoreConfiguration.class)
                .withContext(getApplicationContext())
                .usingPassphrase(passphrase)
                .forClass(Credential.class)
                .store();

        store.openSync();
    }

}
