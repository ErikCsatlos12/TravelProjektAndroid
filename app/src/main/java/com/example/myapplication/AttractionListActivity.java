package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class AttractionListActivity extends BaseActivity implements AttractionAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> fullAttractionList;
    private FirebaseFirestore db;

    private String currentCategoryFilter = "Mind mutatása";
    private String currentPriceFilter = "all";
    private String currentSearchQuery = "";

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private Location userLastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.attractions_recycler_view);
        db = FirebaseFirestore.getInstance();
        fullAttractionList = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String initialFilter = getIntent().getStringExtra("INITIAL_FILTER");
        if (initialFilter != null) {
            currentCategoryFilter = initialFilter;
        }

        if (!currentCategoryFilter.equals("Mind mutatása")) {
            setTitle(currentCategoryFilter);
        } else {
            setTitle(getString(R.string.category_all));
        }

        loadDataFromFirebase();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    currentSearchQuery = query;
                    applyFilters();
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentSearchQuery = newText;
                    applyFilters();
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_filter) {
            showFilterDialog();
            return true;
        }

        if (id == R.id.action_map) {
            Intent intent = new Intent(AttractionListActivity.this, MapsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadDataFromFirebase() {
        Toast.makeText(this, getString(R.string.toast_loading), Toast.LENGTH_SHORT).show();

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
                                    } else if ("Walk".equals(category)) {
                                        attraction = document.toObject(Seta.class);
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
                Toast.makeText(this, getString(R.string.toast_location_permission_denied), Toast.LENGTH_LONG).show();
                applyFilters();
            }
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            applyFilters();
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
                        applyFilters();
                    }
                });
    }

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

        if (userLastLocation != null) {

            for (Attraction attraction : data) {
                double dist = calculateDistance(
                        userLastLocation.getLatitude(), userLastLocation.getLongitude(),
                        attraction.getLatitude(), attraction.getLongitude()
                );
                attraction.setDistanceToUser(dist);
            }

            Collections.sort(data, new Comparator<Attraction>() {
                @Override
                public int compare(Attraction a1, Attraction a2) {
                    return Double.compare(a1.getDistanceToUser(), a2.getDistanceToUser());
                }
            });
        }

        adapter = new AttractionAdapter(data);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void showFilterDialog() {
        final String[] filterKeys = {"all", "free", "paid"};

        final String[] filterOptions = {
                getString(R.string.filter_option_all),
                getString(R.string.filter_option_free),
                getString(R.string.filter_option_paid)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String dialogTitle = getString(R.string.filter_dialog_title);
        String categoryDisplay = currentCategoryFilter;

        if (currentCategoryFilter.equals("Mind mutatása")) {
            categoryDisplay = getString(R.string.filter_option_all);
        }

        builder.setTitle(dialogTitle + " (" + categoryDisplay + ")");

        builder.setItems(filterOptions, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                currentPriceFilter = filterKeys[which];
                Toast.makeText(AttractionListActivity.this, getString(R.string.toast_filter_set) + " " + filterOptions[which], Toast.LENGTH_SHORT).show();
                applyFilters();
            }
        });

        builder.setNegativeButton(getString(R.string.dialog_button_cancel), new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void applyFilters() {
        List<Attraction> filteredList = new ArrayList<>();
        String query = currentSearchQuery.toLowerCase(Locale.getDefault());

        for (Attraction attraction : fullAttractionList) {

            boolean categoryMatch = false;
            if (currentCategoryFilter.equals("Mind mutatása")) {
                categoryMatch = true;
            } else if (currentCategoryFilter.equals(getString(R.string.category_historical)) && attraction instanceof HistoricalSite) {
                categoryMatch = true;
            } else if (currentCategoryFilter.equals(getString(R.string.category_natural)) && attraction instanceof NaturalWonder) {
                categoryMatch = true;
            } else if (currentCategoryFilter.equals(getString(R.string.category_adventure)) && attraction instanceof AdventureSite) {
                categoryMatch = true;
            } else if (currentCategoryFilter.equals(getString(R.string.category_walk)) && attraction instanceof Seta) {
                categoryMatch = true;
            }

            if (!categoryMatch) continue;

            boolean priceMatch = false;
            if (currentPriceFilter.equals("all")) {
                priceMatch = true;
            } else if (attraction instanceof Dijkoteles) {
                double ar = ((Dijkoteles) attraction).getAr();
                if (currentPriceFilter.equals("free") && ar == 0.0) {
                    priceMatch = true;
                } else if (currentPriceFilter.equals("paid") && ar > 0.0) {
                    priceMatch = true;
                }
            } else if (currentPriceFilter.equals("free")) {
                priceMatch = true;
            }

            if (!priceMatch) continue;

            boolean searchMatch = false;
            if (query.isEmpty()) {
                searchMatch = true;
            } else {
                if (attraction.getLocalizedName(this).toLowerCase(Locale.getDefault()).contains(query) ||
                        attraction.getLocalizedCity(this).toLowerCase(Locale.getDefault()).contains(query) ||
                        attraction.getCategory(this).toLowerCase(Locale.getDefault()).contains(query)) {
                    searchMatch = true;
                }
            }

            if (!searchMatch) continue;

            filteredList.add(attraction);
        }

        loadRecyclerViewData(filteredList);
        Toast.makeText(this, getString(R.string.toast_results_found, filteredList.size()), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onItemClick(Attraction attraction) {
        Intent intent = new Intent(this, AttractionDetailActivity.class);

        intent.putExtra("NAME", attraction.getLocalizedName(this));
        intent.putExtra("CITY", attraction.getLocalizedCity(this));
        intent.putExtra("DESCRIPTION", attraction.getLocalizedDescription(this));
        intent.putExtra("IMAGE_NAME", attraction.getImageName());
        intent.putExtra("RATING", attraction.getRating());
        intent.putExtra("CATEGORY", attraction.getCategory(this));
        intent.putExtra("LATITUDE", attraction.getLatitude());
        intent.putExtra("LONGITUDE", attraction.getLongitude());

        if (attraction instanceof Seta) {
            Seta seta = (Seta) attraction;
            if (seta.getRoutePoints() != null && !seta.getRoutePoints().isEmpty()) {
                ArrayList<Double> lats = new ArrayList<>();
                ArrayList<Double> lngs = new ArrayList<>();
                for (GeoPoint point : seta.getRoutePoints()) {
                    lats.add(point.getLatitude());
                    lngs.add(point.getLongitude());
                }
                intent.putExtra("ROUTE_LATS", lats);
                intent.putExtra("ROUTE_LNGS", lngs);
            }
        }

        startActivity(intent);
    }
}