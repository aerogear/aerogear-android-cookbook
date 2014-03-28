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
package org.jboss.aerogear.cookbook.datamanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.DataManager;
import org.jboss.aerogear.android.impl.datamanager.SQLStore;
import org.jboss.aerogear.android.impl.datamanager.StoreConfig;
import org.jboss.aerogear.android.impl.datamanager.StoreTypes;
import org.jboss.aerogear.cookbook.CarAdapter;
import org.jboss.aerogear.cookbook.R;
import org.jboss.aerogear.cookbook.model.Car;

import java.util.ArrayList;
import java.util.List;

public class HowToUseSQLStorage extends ListActivity {

    private static final int DIALOG_ADD = 1;
    private SQLStore<Car> store;
    private IncrementalIdGenerator idGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_list);

        StoreConfig storeConfig = buildStoreConfig();
        DataManager dataManager = new DataManager();
        store = (SQLStore<Car>) dataManager.store("myStorage", storeConfig);
        store.open(new Callback<SQLStore<Car>>() {
            @Override
            public void onSuccess(SQLStore<Car> data) {
                updateDisplayCars();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(HowToUseSQLStorage.this, getString(R.string.error_open_database), Toast.LENGTH_SHORT).show();
            }
        });

        ListView listview = getListView();
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Car car = (Car) adapterView.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(HowToUseSQLStorage.this)
                        .setTitle(getString(R.string.delete_confirmation))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                store.remove(car.getId());
                                updateDisplayCars();
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

    private StoreConfig buildStoreConfig() {
        StoreConfig storeConfig = new StoreConfig(Car.class);
        storeConfig.setContext(getApplicationContext());
        storeConfig.setType(StoreTypes.SQL);
        idGenerator = new IncrementalIdGenerator(readLastSQLStoreIdFromSharedPreference());
        storeConfig.setIdGenerator(idGenerator);
        return storeConfig;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveLastSQLStoreIdInSharedPreference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_car, menu);
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
        final View view = inflater.inflate(R.layout.dialog_car, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Car car = retrieveCarFromForm(view);
                        store.save(car);
                        updateDisplayCars();
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

    private Long readLastSQLStoreIdFromSharedPreference() {
        SharedPreferences prefs = this.getSharedPreferences("AeroGear", Context.MODE_PRIVATE);
        return prefs.getLong("lastSqlStoreId", 0L);
    }

    private void saveLastSQLStoreIdInSharedPreference() {
        SharedPreferences.Editor editor = getSharedPreferences("AeroGear", MODE_PRIVATE).edit();
        editor.putLong("lastSqlStoreId", idGenerator.getActualValue());
        editor.commit();
    }

    private void updateDisplayCars() {
        List<Car> allCars = new ArrayList<Car>(store.readAll());
        this.setListAdapter(new CarAdapter(this, allCars));
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
