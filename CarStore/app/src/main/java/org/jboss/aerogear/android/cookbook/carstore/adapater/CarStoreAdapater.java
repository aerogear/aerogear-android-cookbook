package org.jboss.aerogear.android.cookbook.carstore.adapater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.listview_car_store_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Car car = getItem(position);
        holder.carBrand.setText(car.getBrand());
        holder.carPrice.setText(String.valueOf(car.getPrice()));

        return convertView;
    }

    private class ViewHolder {

        private final TextView carBrand;
        private final TextView carPrice;

        ViewHolder(View view) {
            carBrand = (TextView) view.findViewById(R.id.brand);
            carPrice = (TextView) view.findViewById(R.id.price);
        }

    }

}
