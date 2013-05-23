package org.jboss.aerogear.cookbook.datamanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import org.jboss.aerogear.android.impl.datamanager.MemoryStorage;
import org.jboss.aerogear.cookbook.CarAdapter;
import org.jboss.aerogear.cookbook.GuideApplication;
import org.jboss.aerogear.cookbook.R;
import org.jboss.aerogear.cookbook.model.Car;

import java.util.ArrayList;
import java.util.List;

public class HowToUseMemoryStorage extends ListActivity {

    private static final int DIALOG_ADD = 1;

    private MemoryStorage<Car> store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_list);

        store = ((GuideApplication) getApplicationContext()).getMemoryStorage();

        ListView listview = getListView();
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Car car = (Car) adapterView.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(HowToUseMemoryStorage.this)
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

    @Override
    protected void onStart() {
        super.onStart();
        updateDisplayCars();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_car, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.add ) {
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

        return car;
    }

}
