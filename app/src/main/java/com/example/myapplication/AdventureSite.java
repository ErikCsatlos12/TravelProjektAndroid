package com.example.myapplication;

import android.content.Context;
import java.util.Map;

public class AdventureSite extends Attraction implements Dijkoteles {

    private Map<String, String> activityType;
    private double price;

    public AdventureSite() {
        super();
    }

    public AdventureSite(Map<String, String> name, Map<String, String> city, double rating, double latitude, double longitude, Map<String, String> description, String imageName, Map<String, String> activityType, double price) {
        super(name, city, rating, latitude, longitude, description, imageName);
        this.activityType = activityType;
        this.price = price;
    }

    @Override
    public String getCategory(Context context) {
        String localizedActivityType = getLocalizedValue(this.activityType, context);
        return context.getString(R.string.category_adventure_format, localizedActivityType);
    }

    public Map<String, String> getActivityType() {
        return activityType;
    }

    public void setActivityType(Map<String, String> activityType) {
        this.activityType = activityType;
    }

    public String getLocalizedActivityType(Context context) {
        return getLocalizedValue(this.activityType, context);
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