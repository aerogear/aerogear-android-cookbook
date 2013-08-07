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
package org.jboss.aerogear.cookbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.jboss.aerogear.cookbook.model.Car;

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

        TextView model = (TextView) carView.findViewById(R.id.brand);
        model.setText(car.getBrand());

        TextView price = (TextView) carView.findViewById(R.id.price);
        price.setText(String.valueOf(car.getPrice()));

        return carView;

    }

}