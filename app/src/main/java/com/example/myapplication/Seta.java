package com.example.myapplication;

import android.content.Context;
import com.google.firebase.firestore.GeoPoint;
import java.util.List;
import java.util.Map;

public class Seta extends Attraction implements Dijkoteles {

    private List<GeoPoint> waypoints;
    private Map<String, String> difficulty;

    public Seta() {
        super();
    }

    public Seta(Map<String, String> name, Map<String, String> city, double rating, double latitude, double longitude,
                Map<String, String> description, String imageName,
                List<GeoPoint> waypoints, Map<String, String> difficulty) {

        super(name, city, rating, latitude, longitude, description, imageName);
        this.waypoints = waypoints;
        this.difficulty = difficulty;
    }

    @Override
    public String getCategory(Context context) {
        String localizedDifficulty = getLocalizedValue(this.difficulty, context);
        return context.getString(R.string.category_walk_format, localizedDifficulty);
    }

    public List<GeoPoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<GeoPoint> waypoints) {
        this.waypoints = waypoints;
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