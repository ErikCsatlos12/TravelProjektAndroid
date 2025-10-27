package com.example.myapplication;

import com.google.firebase.firestore.PropertyName; // Fontos import a Firebase-hez

public abstract class Attraction {

    private String name;
    private String city;
    private double rating;
    private double latitude;
    private double longitude;
    private String description;
    private String imageName;


    public Attraction() {}

    protected Attraction(String name, String city, double rating, double latitude, double longitude, String description, String imageName) {
        this.name = name;
        this.city = city;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.imageName = imageName;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }



    @PropertyName("lat")
    public double getLatitude() {
        return latitude;
    }

    @PropertyName("lat")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @PropertyName("lng")
    public double getLongitude() {
        return longitude;
    }

    @PropertyName("lng")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public abstract String getCategory();
}