package com.example.myapplication;

import android.content.Context;

public class AdventureSite extends Attraction implements Dijkoteles {

    private String activityType;
    private double price;

    public AdventureSite() {}

    public AdventureSite(String name, String city, double rating, double latitude, double longitude,
                         String description, String imageName,
                         String activityType, double price) {

        super(name, city, rating, latitude, longitude, description, imageName);
        this.activityType = activityType;
        this.price = price;
    }

    @Override
    public String getCategory(Context context) {
        return context.getString(R.string.category_adventure_format, activityType);
    }


    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
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