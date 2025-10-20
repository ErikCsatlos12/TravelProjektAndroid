// Attraction.java
package com.example.myapplication;


public abstract class Attraction {


    private String name;
    private String city;
    private double rating;


    protected Attraction(String name, String city, double rating) {
        this.name = name;
        this.city = city;
        this.rating = rating;
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
    public abstract String getCategory();
}