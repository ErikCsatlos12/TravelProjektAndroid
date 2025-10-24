package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AttractionAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> attractionsDataList;
    private AttractionDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.attractions_recycler_view);

        try {
            dbHelper = new AttractionDatabaseHelper(this);
            attractionsDataList = dbHelper.getAllAttractions();

            if (attractionsDataList.isEmpty()) {
                Log.d("MainActivity", "Adatbázis üres, helyi fájl olvasása indítása...");
                FetchDataTask task = new FetchDataTask(dbHelper, this);
                task.execute();
            } else {
                Log.d("MainActivity", "Adatbázis betöltve, " + attractionsDataList.size() + " elem.");
                adapter = new AttractionAdapter(attractionsDataList);

                adapter.setOnItemClickListener(this);

                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            }

        } catch (Exception e) {
            Log.e("MainActivity", "Hiba történt az adatbázis kezelésekor!", e);
            e.printStackTrace();
        }

        FloatingActionButton fabMap = findViewById(R.id.fab_map);
        fabMap.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onItemClick(Attraction attraction) {
        Intent intent = new Intent(this, AttractionDetailActivity.class);

        intent.putExtra("NAME", attraction.getName());
        intent.putExtra("CITY", attraction.getCity());
        intent.putExtra("DESCRIPTION", attraction.getDescription());
        intent.putExtra("IMAGE_NAME", attraction.getImageName());
        intent.putExtra("RATING", attraction.getRating());
        intent.putExtra("CATEGORY", attraction.getCategory());
        intent.putExtra("LATITUDE", attraction.getLatitude());
        intent.putExtra("LONGITUDE", attraction.getLongitude());

        startActivity(intent);
    }

    private class FetchDataTask extends AsyncTask<Void, Void, List<Attraction>> {

        private AttractionDatabaseHelper dbHelper;
        private Context context;

        public FetchDataTask(AttractionDatabaseHelper helper, Context context) {
            this.dbHelper = helper;
            this.context = context.getApplicationContext();
        }

        @Override
        protected List<Attraction> doInBackground(Void... params) {
            BufferedReader reader = null;
            String jsonString = null;
            List<Attraction> loadedAttractions = new ArrayList<>();

            try {
                InputStream inputStream = context.getResources().openRawResource(R.raw.attractions);
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonString = buffer.toString();

                JSONArray attractionsArray = new JSONArray(jsonString);

                for (int i = 0; i < attractionsArray.length(); i++) {
                    JSONObject attrJson = attractionsArray.getJSONObject(i);

                    String name = attrJson.getString("name");
                    String city = attrJson.getString("city");
                    double rating = attrJson.getDouble("rating");
                    String category = attrJson.getString("category");
                    double price = attrJson.getDouble("price");
                    double lat = attrJson.getDouble("lat");
                    double lng = attrJson.getDouble("lng");
                    String description = attrJson.getString("description");
                    String imageName = attrJson.getString("imageName");

                    if ("Historical".equals(category)) {
                        int year = attrJson.getInt("year");
                        HistoricalSite site = new HistoricalSite(name, city, rating, lat, lng, description, imageName, year, price);
                        loadedAttractions.add(site);

                    } else if ("Natural".equals(category)) {
                        String type = attrJson.getString("type");
                        NaturalWonder wonder = new NaturalWonder(name, city, rating, lat, lng, description, imageName, type, price);
                        loadedAttractions.add(wonder);
                    }
                }
                return loadedAttractions;

            } catch (IOException e) {
                Log.e("FetchDataTask", "Hiba a fájl olvasásánál", e);
                return null;
            } catch (JSONException e) {
                Log.e("FetchDataTask", "Hiba a JSON feldogozásánál", e);
                return null;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("FetchDataTask", "Hiba a reader bezárásakor", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(List<Attraction> loadedAttractions) {
            super.onPostExecute(loadedAttractions);

            if (loadedAttractions != null && !loadedAttractions.isEmpty()) {

                for (Attraction attr : loadedAttractions) {
                    dbHelper.addAttraction(attr);
                }

                attractionsDataList = dbHelper.getAllAttractions();

                adapter = new AttractionAdapter(attractionsDataList);

                adapter.setOnItemClickListener(MainActivity.this);

                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(adapter);

                Log.d("MainActivity", "Fájlból olvasás kész, adatbázis feltöltve " + attractionsDataList.size() + " elemmel.");
            } else {
                Log.e("MainActivity", "Adatok olvasása sikertelen.");
            }
        }
    }
}