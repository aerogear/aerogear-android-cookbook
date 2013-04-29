package org.jboss.aerogear.guides.model;

import org.jboss.aerogear.android.RecordId;

public class Car {

    @RecordId
    private Long id;
    private String manufacturer;
    private String model;
    private Integer price;

    public Car(Long id, String manufacturer, String model, int price) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return manufacturer + " - " + model + " - " + price;
    }
}
