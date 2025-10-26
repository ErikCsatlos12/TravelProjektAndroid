package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.app.AlertDialog;
import android.widget.Toast;
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

public class AttractionListActivity extends AppCompatActivity implements AttractionAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> attractionsDataList;
    private AttractionDatabaseHelper dbHelper;

    private String currentFilter = "Mind mutatása";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        recyclerView = findViewById(R.id.attractions_recycler_view);

        // 1. Megkapjuk az indító Activity-től átadott szűrőt (EZ A KEZDETI KATEGÓRIA)
        String initialFilter = getIntent().getStringExtra("INITIAL_FILTER");
        if (initialFilter != null) {
            currentFilter = initialFilter;
        }

        try {
            dbHelper = new AttractionDatabaseHelper(this);
            attractionsDataList = dbHelper.getAllAttractions();

            if (attractionsDataList.isEmpty()) {
                Log.d("AttractionListActivity", "Adatbázis üres, helyi fájl olvasása indítása...");
                FetchDataTask task = new FetchDataTask(dbHelper, this);
                task.execute();
            } else {
                Log.d("AttractionListActivity", "Adatbázis betöltve, " + attractionsDataList.size() + " elem.");
                // Kezdeti szűrés és betöltés a kategória szerint
                filterAndReloadData("Mind mutatása");
            }

        } catch (Exception e) {
            Log.e("AttractionListActivity", "Hiba történt az adatbázis kezelésekor!", e);
            e.printStackTrace();
        }

        // Térkép gomb (Jobb lent)
        FloatingActionButton fabMap = findViewById(R.id.fab_map);
        fabMap.setOnClickListener(view -> {
            Intent intent = new Intent(AttractionListActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        // SZŰRŐ GOMB (Bal lent)
        FloatingActionButton fabFilter = findViewById(R.id.fab_filter);
        fabFilter.setOnClickListener(v -> {
            showFilterDialog();
        });

        // VISSZA A FŐMENÜBE GOMB BEÁLLÍTÁSA
        FloatingActionButton fabBackHome = findViewById(R.id.fab_back_home);
        fabBackHome.setOnClickListener(v -> {
            finish();
        });
    }


    private void loadRecyclerViewData(List<Attraction> data) {
        adapter = new AttractionAdapter(data);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void showFilterDialog() {
        final String[] filterOptions = {"Mind mutatása", "Ingyenes", "Fizetős"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Szűrés Ár Szerint (" + currentFilter + ")");

        builder.setItems(filterOptions, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                String selectedFilter = filterOptions[which];
                Toast.makeText(AttractionListActivity.this, "Ár szűrő beállítva: " + selectedFilter, Toast.LENGTH_SHORT).show();

                filterAndReloadData(selectedFilter);
            }
        });

        builder.setNegativeButton("Mégse", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    /**
     * Kiszűri a látványosságokat a teljes listából, és frissíti a RecyclerView-t.
     */
    private void filterAndReloadData(String priceFilter) {
        List<Attraction> fullList = dbHelper.getAllAttractions();
        List<Attraction> categoryFilteredList = new ArrayList<>();

        // 1. Első lépés: KATEGÓRIA SZŰRÉS (Mindig az initialFilter szerint)
        for (Attraction attraction : fullList) {
            if (currentFilter.equals("Mind mutatása")) {
                categoryFilteredList.add(attraction);
            } else if (currentFilter.equals("Történelmi helyszín") && attraction instanceof HistoricalSite) {
                categoryFilteredList.add(attraction);
            } else if (currentFilter.equals("Természeti csoda") && attraction instanceof NaturalWonder) {
                categoryFilteredList.add(attraction);
            } else if (currentFilter.equals("Adventure") && attraction instanceof AdventureSite) { // <-- KALAND KATEGÓRIA TÁMOGATÁS
                categoryFilteredList.add(attraction);
            }
        }

        // 2. Második lépés: ÁR SZERINTI SZŰRÉS a már szűrt listán
        List<Attraction> finalFilteredList = new ArrayList<>();

        for (Attraction attraction : categoryFilteredList) {
            boolean isPriceMatch = false;

            if (priceFilter.equals("Mind mutatása")) {
                isPriceMatch = true;
            } else if (attraction instanceof Dijkoteles) {
                double ar = ((Dijkoteles) attraction).getAr();

                if (priceFilter.equals("Ingyenes") && ar == 0.0) {
                    isPriceMatch = true;
                } else if (priceFilter.equals("Fizetős") && ar > 0.0) {
                    isPriceMatch = true;
                }
            } else if (priceFilter.equals("Ingyenes")) {
                isPriceMatch = true; // Ingyenes, ha nem díjköteles
            }

            if (isPriceMatch) {
                finalFilteredList.add(attraction);
            }
        }

        // Frissítjük az Adaptert az új, szűrt listával
        loadRecyclerViewData(finalFilteredList);

        Toast.makeText(this, "Találatok: " + finalFilteredList.size(), Toast.LENGTH_SHORT).show();
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

                    // Ellenőrizzük, hogy az Adventure kategóriához tartozó egyedi mezők léteznek-e
                    String activityType = "";
                    if (category.equals("Adventure")) {
                        // A kaland kategóriánál az activityType az AdventureSite konstruktorához szükséges
                        if (attrJson.has("activityType")) {
                            activityType = attrJson.getString("activityType");
                        } else {
                            Log.e("FetchDataTask", "Hiányzó activityType mező az Adventure kategóriánál!");
                            continue; // Kihagyjuk ezt az elemet, ha hibás
                        }
                    }

                    if ("Historical".equals(category)) {
                        int year = attrJson.getInt("year");
                        HistoricalSite site = new HistoricalSite(name, city, rating, lat, lng, description, imageName, year, price);
                        loadedAttractions.add(site);

                    } else if ("Natural".equals(category)) {
                        String type = attrJson.getString("type");
                        NaturalWonder wonder = new NaturalWonder(name, city, rating, lat, lng, description, imageName, type, price);
                        loadedAttractions.add(wonder);
                    } else if ("Adventure".equals(category)) {
                        AdventureSite adventure = new AdventureSite(name, city, rating, lat, lng, description, imageName, activityType, price);
                        loadedAttractions.add(adventure);
                    }
                }
                return loadedAttractions;

            } catch (IOException e) {
                Log.e("FetchDataTask", "Hiba a fájl olvasásánál", e);
                return null;
            } catch (JSONException e) {
                Log.e("FetchDataTask", "Hiba a JSON feldogozásánál: " + e.getMessage(), e);
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

                loadRecyclerViewData(attractionsDataList);

                Log.d("AttractionListActivity", "Fájlból olvasás kész, adatbázis feltöltve " + attractionsDataList.size() + " elemmel.");
            } else {
                Log.e("AttractionListActivity", "Adatok olvasása sikertelen.");
            }
        }
    }
}