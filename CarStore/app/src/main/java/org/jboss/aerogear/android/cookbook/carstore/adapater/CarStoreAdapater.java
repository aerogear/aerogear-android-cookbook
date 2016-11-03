package org.jboss.aerogear.android.cookbook.carstore.adapater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.jboss.aerogear.android.cookbook.carstore.R;
import org.jboss.aerogear.android.cookbook.carstore.model.Car;

import java.util.List;

public class CarStoreAdapater extends BaseAdapter {

    private final Context context;
    private final List<Car> carList;

    public CarStoreAdapater(Context context, List<Car> carList) {
        this.context = context;
        this.carList = carList;
    }

    @Override
    public int getCount() {
        return carList.size();
    }

    @Override
    public Car getItem(int position) {
        return carList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.listview_car_store_item, null);

        Car car = getItem(position);

        TextView carBrand = (TextView) view.findViewById(R.id.brand);
        carBrand.setText(car.getBrand());

        TextView carPrice = (TextView) view.findViewById(R.id.price);
        carPrice.setText(String.valueOf(car.getPrice()));

        return view;
    }

}
