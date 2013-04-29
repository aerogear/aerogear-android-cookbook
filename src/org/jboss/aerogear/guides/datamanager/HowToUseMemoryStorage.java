package org.jboss.aerogear.guides.datamanager;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import org.jboss.aerogear.android.DataManager;
import org.jboss.aerogear.android.impl.datamanager.MemoryStorage;
import org.jboss.aerogear.android.impl.datamanager.StoreConfig;
import org.jboss.aerogear.android.impl.datamanager.StoreTypes;
import org.jboss.aerogear.guides.R;
import org.jboss.aerogear.guides.model.Car;

import java.util.ArrayList;
import java.util.List;

public class HowToUseMemoryStorage extends ListActivity {

    private MemoryStorage<Car> store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_list);

        DataManager dataManager = new DataManager();

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setContext(getApplicationContext());
        storeConfig.setType(StoreTypes.MEMORY);
        storeConfig.setKlass(Car.class);

        store = (MemoryStorage) dataManager.store("carStore", storeConfig);

        // Put inicial data
        store.save(new Car(1L, "Porsche", "911", 135000));
        store.save(new Car(2L, "Nissan", "GT-R", 80000));
        store.save(new Car(3L, "BMW", "M3", 60500));
        store.save(new Car(4L, "Audi", "S5", 53000));
        store.save(new Car(5L, "Audi", "TT", 40000));
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateDisplayCars();
    }

    private void updateDisplayCars() {
        List<Car> allCars = new ArrayList<Car>(store.readAll());
        this.setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, allCars));
    }
}
