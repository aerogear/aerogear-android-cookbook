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
package org.jboss.aerogear.cookbook;

import android.app.Application;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.jboss.aerogear.android.DataManager;
import org.jboss.aerogear.android.impl.datamanager.MemoryStorage;
import org.jboss.aerogear.android.impl.datamanager.StoreConfig;
import org.jboss.aerogear.android.impl.datamanager.StoreTypes;
import org.jboss.aerogear.android.unifiedpush.PushConfig;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.jboss.aerogear.cookbook.datamanager.IncrementalIdGenerator;
import org.jboss.aerogear.cookbook.model.Car;
import static org.jboss.aerogear.cookbook.push.PushConstants.GCM_SENDER_ID;
import static org.jboss.aerogear.cookbook.push.PushConstants.MY_ALIAS;
import static org.jboss.aerogear.cookbook.push.PushConstants.SECRET;
import static org.jboss.aerogear.cookbook.push.PushConstants.UNIFIED_PUSH_URL;
import static org.jboss.aerogear.cookbook.push.PushConstants.VARIANT_ID;

public class GuideApplication extends Application {

    private MemoryStorage memoryStorage;
    private PushRegistrar registrar;
    private PushConfig config;
    private final Registrations registrations = new Registrations();

    @Override
    public void onCreate() {
        super.onCreate();
        createMemoryStorage();
    }

    private void createMemoryStorage() {

        DataManager dataManager = new DataManager();

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setContext(getApplicationContext());
        storeConfig.setType(StoreTypes.MEMORY);
        storeConfig.setKlass(Car.class);

        storeConfig.setIdGenerator(new IncrementalIdGenerator());

        memoryStorage = (MemoryStorage) dataManager.store("carStore", storeConfig);

        // Put inicial data
        memoryStorage.save(new Car("Porsche", "911", 135000));
        memoryStorage.save(new Car("Nissan", "GT-R", 80000));
        memoryStorage.save(new Car("BMW", "M3", 60500));
        memoryStorage.save(new Car("Audi", "S5", 53000));
        memoryStorage.save(new Car("Audi", "TT", 40000));

    }

    public MemoryStorage<Car> getMemoryStorage() {
        return memoryStorage;
    }

    public void enablePush() {

        if (registrar == null) {
            try {
                config = new PushConfig(new URI(UNIFIED_PUSH_URL), GCM_SENDER_ID);
                config.setVariantID(VARIANT_ID);
                config.setSecret(SECRET);
                config.setAlias(MY_ALIAS);

                registrar = registrations.push("registrar", config);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public PushRegistrar getRegistrar() {
        if (registrar == null) {
            enablePush();
        }
        return registrar;
    }
}
