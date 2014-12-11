package org.jboss.aerogear.android.cookbook.carstore.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jboss.aerogear.android.cookbook.carstore.CarStoreApplication;
import org.jboss.aerogear.android.cookbook.carstore.R;
import org.jboss.aerogear.android.cookbook.carstore.model.Car;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public class CarStoreActivity extends ActionBarActivity {

    private static final int DIALOG_ADD = 1;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(CarStoreActivity.this)
                        .setTitle(getString(R.string.delete_confirmation))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                remove(car);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplayCars();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_car_store, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            showDialog(DIALOG_ADD);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_car_store, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
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
                });
        return builder.create();
    }

    private void updateDisplayCars() {
        Collection<Car> cars = storeApplication.getStore().readAll();
        ArrayAdapter<Car> adapater = new ArrayAdapter<Car>(this,
                android.R.layout.simple_list_item_1, new ArrayList<>(cars));
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
