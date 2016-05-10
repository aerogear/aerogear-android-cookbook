package org.jboss.aerogear.android.cookbook.carstore.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.jboss.aerogear.android.cookbook.carstore.CarStoreApplication;
import org.jboss.aerogear.android.cookbook.carstore.R;
import org.jboss.aerogear.android.cookbook.carstore.adapater.CarStoreAdapater;
import org.jboss.aerogear.android.cookbook.carstore.model.Car;

import java.util.ArrayList;
import java.util.Collection;


public class CarStoreActivity extends AppCompatActivity {

    private CarStoreApplication storeApplication;
    private ListView carList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_store);

        storeApplication = (CarStoreApplication) getApplication();
        carList = (ListView) findViewById(R.id.carList);

        carList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Car car = (Car) adapterView.getItemAtPosition(position);
                showDeleteConfirmationDialog(car);
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormDialog();
            }
        });
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
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Car car = retrieveCarFromForm(view);
                        save(car);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .create();

        dialog.getWindow().getAttributes().windowAnimations = R.style.MyTheme_DialogAnimation;
        dialog.show();
    }

    private void showDeleteConfirmationDialog(final Car car) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_confirmation))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        remove(car);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void updateDisplayCars() {
        Collection<Car> cars = storeApplication.getStore().readAll();
        CarStoreAdapater adapater = new CarStoreAdapater(getApplicationContext(), new ArrayList<>(cars));
        carList.setAdapter(adapater);
    }

    private void save(Car car) {
        storeApplication.getStore().save(car);
        updateDisplayCars();
    }

    private void remove(Car car) {
        storeApplication.getStore().remove(car.getId());
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
