package com.example.myapplication;

public class HistoricalSite extends Attraction implements Dijkoteles {

    private int constructionYear;
    private double price;

    public HistoricalSite() {}


    public HistoricalSite(String name, String city, double rating, double latitude, double longitude,
                          String description, String imageName,
                          int constructionYear, double price) {

        super(name, city, rating, latitude, longitude, description, imageName);
        this.constructionYear = constructionYear;
        this.price = price;
    }

    @Override
    public String getCategory() {
        return "Történelmi helyszín";
    }

    public int getConstructionYear() {
        return constructionYear;
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