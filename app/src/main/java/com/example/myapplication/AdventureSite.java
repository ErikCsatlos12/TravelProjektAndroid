package com.example.myapplication;

public class AdventureSite extends Attraction implements Dijkoteles {

    private String activityType; // Pl: "bobpálya", "kalandpark", "síelés"
    private double price;

    public AdventureSite(String name, String city, double rating, double latitude, double longitude,
                         String description, String imageName,
                         String activityType, double price) {

        super(name, city, rating, latitude, longitude, description, imageName);
        this.activityType = activityType;
        this.price = price;
    }

    @Override
    public String getCategory() {

        return "Kaland (" + activityType + ")";
    }

    public String getActivityType() {
        return activityType;
    }

    @Override
    public double getAr() {
        return this.price;
    }

    @Override
    public String getArInfo() {
        if (price == 0) {
            return "Ingyenes";
        }
        return price + " RON";
    }
}