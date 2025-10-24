package com.example.myapplication;


public class NaturalWonder extends Attraction implements Dijkoteles {

    private String type;
    private double price;

    public NaturalWonder(String name, String city, double rating, String type, double price) {
        super(name, city, rating);
        this.type = type;
        this.price = price;
    }

    @Override
    public String getCategory() {
        return "Természeti csoda (" + type + ")";
    }

    public String getType() {
        return type;
    }


    @Override
    public double getAr() {
        return this.price;
    }


}