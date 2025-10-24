// Attraction.java
package com.example.myapplication;


public abstract class Attraction {


    private String name;
    private String city;
    private double rating;
    private double latitude;
    private double longitude;


    protected Attraction(String name, String city, double rating, double latitude, double longitude) {
        this.name = name;
        this.city = city;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getName() {
        return name;
    }
    public String getCity() {
        return city;
    }
    public double getRating() {
        return rating;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public abstract String getCategory();
}