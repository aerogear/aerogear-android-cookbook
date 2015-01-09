package org.jboss.aerogear.android.cookbook.carstore;

import android.app.Application;

import org.jboss.aerogear.android.cookbook.carstore.model.Car;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;


public class CarStoreApplication extends Application {

    private SQLStore<Car> store;

    @Override
    public void onCreate() {
        super.onCreate();

        DataManager.config("carStore", SQLStoreConfiguration.class)
                .withContext(getApplicationContext())
                .forClass(Car.class)
                .store();

        store = (SQLStore) DataManager.getStore("carStore");
        store.openSync();
    }

    public SQLStore<Car> getStore() {
        return store;
    }

}
