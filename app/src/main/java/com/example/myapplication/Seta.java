package com.example.myapplication;

import android.content.Context;
import com.google.firebase.firestore.GeoPoint;
import java.util.List;
import java.util.Map;

public class Seta extends Attraction implements Dijkoteles {

    private List<GeoPoint> routePoints;
    private Map<String, String> difficulty;

    public Seta() {
        super();
    }

    public Seta(Map<String, String> name, Map<String, String> city, double rating, double latitude, double longitude,
                Map<String, String> description, String imageName,
                List<GeoPoint> routePoints, Map<String, String> difficulty) {

        super(name, city, rating, latitude, longitude, description, imageName);
        this.routePoints = routePoints;
        this.difficulty = difficulty;
    }

    @Override
    public String getCategory(Context context) {
        String localizedDifficulty = getLocalizedValue(this.difficulty, context);
        return context.getString(R.string.category_walk_format, localizedDifficulty);
    }

    public List<GeoPoint> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<GeoPoint> routePoints) {
        this.routePoints = routePoints;
    }

    public Map<String, String> getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Map<String, String> difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public double getAr() {
        return 0.0;
    }

    @Override
    public String getArInfo() {
        return "Ingyenes";
    }
}