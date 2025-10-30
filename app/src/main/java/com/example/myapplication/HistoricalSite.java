package com.example.myapplication;

import android.content.Context; // <-- ÚJ IMPORT

public class HistoricalSite extends Attraction implements Dijkoteles {

    private int year;
    private double price;

    public HistoricalSite() {}

    public HistoricalSite(String name, String city, double rating, double latitude, double longitude,
                          String description, String imageName,
                          int year, double price) {

        super(name, city, rating, latitude, longitude, description, imageName);
        this.year = year;
        this.price = price;
    }

    @Override
    public String getCategory(Context context) {
        return context.getString(R.string.category_historical);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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