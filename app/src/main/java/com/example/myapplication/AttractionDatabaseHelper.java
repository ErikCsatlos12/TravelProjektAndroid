package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AttractionDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "attractions.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_ATTRACTIONS = "attractions";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_NATURE_TYPE = "nature_type";

    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";


    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_ATTRACTIONS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_CITY + " TEXT, " +
                    COLUMN_RATING + " REAL, " +
                    COLUMN_PRICE + " REAL, " +
                    COLUMN_YEAR + " INTEGER, " +
                    COLUMN_NATURE_TYPE + " TEXT, " +
                    COLUMN_LATITUDE + " REAL, " +
                    COLUMN_LONGITUDE + " REAL, " +
                    COLUMN_CATEGORY + " TEXT" +
                    ")";

    public AttractionDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTRACTIONS);
        onCreate(db);
    }

    public void addAttraction(Attraction attraction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, attraction.getName());
        values.put(COLUMN_CITY, attraction.getCity());
        values.put(COLUMN_RATING, attraction.getRating());
        values.put(COLUMN_LATITUDE, attraction.getLatitude());
        values.put(COLUMN_LONGITUDE, attraction.getLongitude());

        if (attraction instanceof HistoricalSite) {
            HistoricalSite site = (HistoricalSite) attraction;
            values.put(COLUMN_CATEGORY, "Historical");
            values.put(COLUMN_YEAR, site.getConstructionYear());
            values.put(COLUMN_PRICE, site.getAr());

        } else if (attraction instanceof NaturalWonder) {
            NaturalWonder wonder = (NaturalWonder) attraction;
            values.put(COLUMN_CATEGORY, "Natural");
            values.put(COLUMN_NATURE_TYPE, wonder.getType());
            values.put(COLUMN_PRICE, wonder.getAr());
        }

        db.insert(TABLE_ATTRACTIONS, null, values);
        db.close();
    }

    public List<Attraction> getAllAttractions() {
        List<Attraction> attractionList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ATTRACTIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Attraction attraction = null;

                int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int cityIndex = cursor.getColumnIndex(COLUMN_CITY);
                int ratingIndex = cursor.getColumnIndex(COLUMN_RATING);
                int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int latIndex = cursor.getColumnIndex(COLUMN_LATITUDE);
                int lngIndex = cursor.getColumnIndex(COLUMN_LONGITUDE);

                if (categoryIndex == -1 || nameIndex == -1 || cityIndex == -1 || ratingIndex == -1 || priceIndex == -1) {
                    continue;
                }

                String category = cursor.getString(categoryIndex);
                String name = cursor.getString(nameIndex);
                String city = cursor.getString(cityIndex);
                double rating = cursor.getDouble(ratingIndex);
                double price = cursor.getDouble(priceIndex);
                double latitude = cursor.getDouble(latIndex);
                double longitude = cursor.getDouble(lngIndex);

                if ("Historical".equals(category)) {
                    int yearIndex = cursor.getColumnIndex(COLUMN_YEAR);
                    if (yearIndex == -1) continue;
                    int year = cursor.getInt(yearIndex);

                    attraction = new HistoricalSite(name, city, rating, latitude, longitude, year, price);

                } else if ("Natural".equals(category)) {
                    int typeIndex = cursor.getColumnIndex(COLUMN_NATURE_TYPE);
                    if (typeIndex == -1) continue;
                    String type = cursor.getString(typeIndex);

                    attraction = new NaturalWonder(name, city, rating, latitude, longitude, type, price);
                }

                if (attraction != null) {
                    attractionList.add(attraction);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return attractionList;
    }
}