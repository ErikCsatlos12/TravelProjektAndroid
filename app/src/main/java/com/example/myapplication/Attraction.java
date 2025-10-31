package com.example.myapplication;

import android.content.Context;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import java.util.HashMap;
import java.util.Map;

public abstract class Attraction {

    @Exclude
    private String documentId;

    private Map<String, String> name;
    private Map<String, String> city;
    private Map<String, String> description;
    private String imageName;
    private double rating;
    private double latitude;
    private double longitude;

    @Exclude
    private double distanceToUser = 0.0;

    public Attraction() {
        this.name = new HashMap<>();
        this.city = new HashMap<>();
        this.description = new HashMap<>();
    }

    public Attraction(Map<String, String> name, Map<String, String> city, double rating, double latitude, double longitude, Map<String, String> description, String imageName) {
        this.name = name;
        this.city = city;
        this.description = description;
        this.imageName = imageName;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected String getLocalizedValue(Map<String, String> localizedMap, Context context) {
        if (localizedMap == null || localizedMap.isEmpty()) return "N/A";
        String lang = LocaleHelper.getLanguage(context);
        if (localizedMap.containsKey(lang)) {
            return localizedMap.get(lang);
        }
        if (localizedMap.containsKey("hu")) {
            return localizedMap.get("hu");
        }
        if (!localizedMap.isEmpty()) {
            return localizedMap.values().iterator().next();
        }
        return "N/A";
    }

    public abstract String getCategory(Context context);

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Map<String, String> getName() { return name; }
    public void setName(Map<String, String> name) { this.name = name; }

    public Map<String, String> getCity() { return city; }
    public void setCity(Map<String, String> city) { this.city = city; }

    public Map<String, String> getDescription() { return description; }
    public void setDescription(Map<String, String> description) { this.description = description; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    @PropertyName("lat")
    public double getLatitude() { return latitude; }
    @PropertyName("lat")
    public void setLatitude(double latitude) { this.latitude = latitude; }

    @PropertyName("lng")
    public double getLongitude() { return longitude; }
    @PropertyName("lng")
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getLocalizedName(Context context) {
        return getLocalizedValue(this.name, context);
    }
    public String getLocalizedCity(Context context) {
        return getLocalizedValue(this.city, context);
    }
    public String getLocalizedDescription(Context context) {
        return getLocalizedValue(this.description, context);
    }

    @Exclude
    public double getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(double distanceToUser) {
        this.distanceToUser = distanceToUser;
    }
}