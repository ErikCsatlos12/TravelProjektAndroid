package com.example.myapplication;

public class NaturalWonder extends Attraction {

    private String type;

    public NaturalWonder(String name, String city, double rating, String type) {
        super(name, city, rating);
        this.type = type;
    }

    @Override
    public String getCategory() {
        return "Természeti csoda (" + type + ")";
    }

    public String getType() {
        return type;
    }
}
