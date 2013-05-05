package org.jboss.aerogear.guides;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.jboss.aerogear.guides.model.Car;

import java.util.List;

public class CarAdapter extends BaseAdapter {

    private final Context context;
    private final List<Car> cars;

    public CarAdapter(Context context, List<Car> cars) {
        this.context = context;
        this.cars = cars;
    }

    @Override
    public int getCount() {
        return cars.size();
    }

    @Override
    public Car getItem(int position) {
        return cars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int positon, View view, ViewGroup viewGroup) {

        Car car = getItem(positon);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View carView = inflater.inflate(R.layout.car_list_item, null);

        TextView manufacturer = (TextView) carView.findViewById(R.id.manufacturer);
        manufacturer.setText(car.getManufacturer());

        TextView model = (TextView) carView.findViewById(R.id.model);
        model.setText(car.getModel());

        TextView price = (TextView) carView.findViewById(R.id.price);
        price.setText(String.valueOf(car.getPrice()));

        return carView;

    }

}