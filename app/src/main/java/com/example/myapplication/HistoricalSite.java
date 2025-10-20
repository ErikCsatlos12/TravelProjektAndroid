package com.example.myapplication;

public class HistoricalSite extends Attraction {


    private int constructionYear;

    public HistoricalSite(String name, String city, double rating, int constructionYear) {


        super(name, city, rating);
        this.constructionYear = constructionYear;
    }

    @Override
    public String getCategory() {
        return "Történelmi helyszín";
    }

    public int getConstructionYear() {
        return constructionYear;
    }

}
