package com.example.myapplication;

import androidx.annotation.NonNull; // Import
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener; // Import
import com.google.android.gms.tasks.Task; // Import
import com.google.firebase.firestore.FirebaseFirestore; // Import
import com.google.firebase.firestore.QueryDocumentSnapshot; // Import
import com.google.firebase.firestore.QuerySnapshot; // Import

import java.util.ArrayList; // Import
import java.util.List; // Import

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FirebaseFirestore db;
    private List<Attraction> attractionsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        attractionsList = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this, "Térkép betöltése a felhőből...", Toast.LENGTH_SHORT).show();

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
                                        attractionsList.add(attraction);
                                    }
                                } catch (Exception e) {
                                    Log.e("MapsActivity_Firebase", "Hiba az objektum átalakításánál: " + document.getId(), e);
                                }
                            }
                            Log.d("MapsActivity", "Firebase letöltés kész: " + attractionsList.size() + " elem.");

                            placeMarkersOnMap();

                        } else {
                            Log.e("MapsActivity", "Hiba a Firebase adatok letöltésekor: ", task.getException());
                        }
                    }
                });
    }

    private void placeMarkersOnMap() {
        if (mMap == null) return;
        for (Attraction attr : attractionsList) {
            LatLng location = new LatLng(attr.getLatitude(), attr.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(attr.getName())
                    .snippet(attr.getCity()));
        }

        if (!attractionsList.isEmpty()) {
            LatLng firstLocation = new LatLng(attractionsList.get(0).getLatitude(), attractionsList.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f));
        } else {
            LatLng targuMures = new LatLng(46.545, 24.5625);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targuMures, 9f));
        }
    }
}