package org.jboss.aerogear.android.cookbook.carstore.model;

import org.jboss.aerogear.android.RecordId;

import java.util.UUID;

public class Car {

    @RecordId
    private UUID id;
    private String manufacturer;
    private String brand;
    private Integer price;
    private String color;

    public Car() {
    }

    public Car(String manufacturer, String brand, int price) {
        this.manufacturer = manufacturer;
        this.brand = brand;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return brand;
    }

}
