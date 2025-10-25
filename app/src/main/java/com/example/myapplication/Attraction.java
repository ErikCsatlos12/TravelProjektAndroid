package com.example.myapplication;
public abstract class Attraction {
    private String name;
    private String city;
    private double rating;
    private double latitude;
    private double longitude;
    private String description;
    private String imageName;

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
    public String getCity() { return city; }
    public double getRating() { return rating; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getDescription() { return description; }
    public String getImageName() { return imageName; }
    public abstract String getCategory();
}