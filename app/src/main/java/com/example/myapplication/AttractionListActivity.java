package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AttractionListActivity extends AppCompatActivity implements AttractionAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> fullAttractionList;
    private FirebaseFirestore db;

    private String currentFilter = "Mind mutatása";

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private Location userLastLocation;
    // ------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        recyclerView = findViewById(R.id.attractions_recycler_view);
        db = FirebaseFirestore.getInstance();
        fullAttractionList = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // GPS inicializálása

        String initialFilter = getIntent().getStringExtra("INITIAL_FILTER");
        if (initialFilter != null) {
            currentFilter = initialFilter;
        }

        loadDataFromFirebase();

        FloatingActionButton fabMap = findViewById(R.id.fab_map);
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttractionListActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fabFilter = findViewById(R.id.fab_filter);
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        FloatingActionButton fabBackHome = findViewById(R.id.fab_back_home);
        fabBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadDataFromFirebase() {
        Toast.makeText(this, "Látványosságok letöltése a felhőből...", Toast.LENGTH_SHORT).show();

        db.collection("attractions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String category = document.getString("category");
                                Attraction attraction = null;

                                try {
                                    if ("Historical".equals(category) || "Cultural".equals(category)) {
                                        attraction = document.toObject(HistoricalSite.class);
                                    } else if ("Natural".equals(category)) {
                                        attraction = document.toObject(NaturalWonder.class);
                                    } else if ("Adventure".equals(category)) {
                                        attraction = document.toObject(AdventureSite.class);
                                    }

                                    if (attraction != null) {
                                        fullAttractionList.add(attraction);
                                    }
                                } catch (Exception e) {
                                    Log.e("FirebaseData", "Hiba az objektum átalakításánál: " + document.getId(), e);
                                }
                            }
                            Log.d("FirebaseData", "Sikeres letöltés: " + fullAttractionList.size() + " elem.");

                            checkLocationPermissionAndGetLocation();

                        } else {
                            Log.e("FirebaseData", "Hiba a Firebase adatok letöltésekor: ", task.getException());
                        }
                    }
                });
    }


    private void checkLocationPermissionAndGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Helymeghatározás nélkül a távolság szerinti rendezés nem lehetséges.", Toast.LENGTH_LONG).show();
                filterAndReloadData("Mind mutatása");
            }
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            filterAndReloadData("Mind mutatása");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            userLastLocation = location;
                            Log.d("Location", "Felhasználó helyzete: " + location.getLatitude() + ", " + location.getLongitude());
                        } else {
                            Log.w("Location", "Nem sikerült utolsó helyzetet lekérni (GPS ki van kapcsolva?).");
                        }
                        filterAndReloadData("Mind mutatása");
                    }
                });
    }

    // Haversine képlet
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void loadRecyclerViewData(List<Attraction> data) {
        // TÁVOLSÁG SZERINTI RENDEZÉS
        if (userLastLocation != null) {
            Collections.sort(data, new Comparator<Attraction>() {
                @Override
                public int compare(Attraction a1, Attraction a2) {
                    double dist1 = calculateDistance(userLastLocation.getLatitude(), userLastLocation.getLongitude(), a1.getLatitude(), a1.getLongitude());
                    double dist2 = calculateDistance(userLastLocation.getLatitude(), userLastLocation.getLongitude(), a2.getLatitude(), a2.getLongitude());
                    return Double.compare(dist1, dist2); // Növekvő sorrend (legközelebbi elöl)
                }
            });
            Toast.makeText(this, "Lista rendezve távolság szerint.", Toast.LENGTH_SHORT).show();
        }

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

    private void filterAndReloadData(String priceFilter) {
        List<Attraction> categoryFilteredList = new ArrayList<>();

        for (Attraction attraction : fullAttractionList) {
            if (currentFilter.equals("Mind mutatása")) {
                categoryFilteredList.add(attraction);
            } else if (currentFilter.equals("Történelmi helyszín") && attraction instanceof HistoricalSite) {
                categoryFilteredList.add(attraction);
            } else if (currentFilter.equals("Természeti csoda") && attraction instanceof NaturalWonder) {
                categoryFilteredList.add(attraction);
            } else if (currentFilter.equals("Adventure") && attraction instanceof AdventureSite) {
                categoryFilteredList.add(attraction);
            }
        }

        // 2. ÁR SZERINTI SZŰRÉS
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
                isPriceMatch = true;
            }

            if (isPriceMatch) {
                finalFilteredList.add(attraction);
            }
        }

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

}