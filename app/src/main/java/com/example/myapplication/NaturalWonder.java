package com.example.myapplication;

import android.content.Context;

public class NaturalWonder extends Attraction implements Dijkoteles {

    private String type;
    private double price;

    public NaturalWonder() {}

    public NaturalWonder(String name, String city, double rating, double latitude, double longitude,
                         String description, String imageName,
                         String type, double price) {

        super(name, city, rating, latitude, longitude, description, imageName);
        this.type = type;
        this.price = price;
    }

    @Override
    public String getCategory(Context context) {
        return context.getString(R.string.category_natural_format, type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public double getAr() {
        return this.price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String getArInfo() {
        if (price == 0) {
            return "Ingyenes";
        }
        return price + " RON";
    }
}