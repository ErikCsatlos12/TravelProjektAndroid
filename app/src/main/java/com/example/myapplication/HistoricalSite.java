package com.example.myapplication;

import android.content.Context;
import java.util.Map;

public class HistoricalSite extends Attraction implements Dijkoteles {

    private Map<String, String> type;
    private double price;

    public HistoricalSite() {
        super();
    }

    public HistoricalSite(Map<String, String> name, Map<String, String> city, double rating, double latitude, double longitude, Map<String, String> description, String imageName, Map<String, String> type, double price) {
        super(name, city, rating, latitude, longitude, description, imageName);
        this.type = type;
        this.price = price;
    }

    @Override
    public String getCategory(Context context) {
        return context.getString(R.string.category_historical) + " (" + getLocalizedValue(this.type, context) + ")";
    }

    public Map<String, String> getType() {
        return type;
    }

    public void setType(Map<String, String> type) {
        this.type = type;
    }

    public String getLocalizedType(Context context) {
        return getLocalizedValue(this.type, context);
    }

    @Override
    public double getAr() {
        return this.price;
    }

    @Override
    public String getArInfo() {
        return (price == 0.0) ? "Ingyenes" : String.format("%.2f RON", price);
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}