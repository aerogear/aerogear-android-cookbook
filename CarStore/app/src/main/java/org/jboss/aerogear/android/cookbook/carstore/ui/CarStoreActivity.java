package org.jboss.aerogear.android.cookbook.carstore.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.jboss.aerogear.android.cookbook.carstore.R;
import org.jboss.aerogear.android.cookbook.carstore.adapater.CarStoreAdapater;
import org.jboss.aerogear.android.cookbook.carstore.model.Car;
import org.jboss.aerogear.android.store.DataManager;
import org.jboss.aerogear.android.store.sql.SQLStore;
import org.jboss.aerogear.android.store.sql.SQLStoreConfiguration;

import java.util.ArrayList;
import java.util.Collection;

public class CarStoreActivity extends AppCompatActivity {

    private SQLStore<Car> carStore;
    private ListView carList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_store);

        carStore = (SQLStore<Car>) DataManager.config("carStore", SQLStoreConfiguration.class)
                .withContext(getApplicationContext())
                .store(Car.class);
        carStore.openSync();

        carList = (ListView) findViewById(R.id.carList);

        carList.setOnItemLongClickListener((adapterView, view, position, id) -> {
            final Car car = (Car) adapterView.getItemAtPosition(position);
            showDeleteConfirmationDialog(car);
            return false;
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v -> showFormDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplayCars();
    }

    private void showFormDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_car_store, null);

        Dialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(getString(R.string.ok), (dialog12, id) -> {
                    Car car = retrieveCarFromForm(view);
                    save(car);
                })
                .setNegativeButton(getString(R.string.cancel), (dialog1, id) -> dialog1.dismiss())
                .create();

        if(dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.MyTheme_DialogAnimation;
        }
        dialog.show();
    }

    private void showDeleteConfirmationDialog(final Car car) {
        new AlertDialog.Builder(this, R.style.MyTheme_Dialog)
                .setTitle(R.string.delete_confirmation)
                .setPositiveButton(R.string.ok, (dialog, id) -> remove(car))
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void updateDisplayCars() {
        Collection<Car> cars = carStore.readAll();
        CarStoreAdapater adapater = new CarStoreAdapater(getApplicationContext(), new ArrayList<>(cars));
        carList.setAdapter(adapater);
    }

    private void save(Car car) {
        carStore.save(car);
        updateDisplayCars();
    }

    private void remove(Car car) {
        carStore.remove(car.getId());
        updateDisplayCars();
    }

    private Car retrieveCarFromForm(View view) {
        TextView manufacturer = (TextView) view.findViewById(R.id.manufacturer);
        TextView model = (TextView) view.findViewById(R.id.brand);
        TextView price = (TextView) view.findViewById(R.id.price);

        Car car = new Car();
        car.setManufacturer(manufacturer.getText().toString());
        car.setBrand(model.getText().toString());
        car.setPrice(Integer.valueOf(price.getText().toString()));

        manufacturer.setText("");
        model.setText("");
        price.setText("");

        return car;
    }

}
