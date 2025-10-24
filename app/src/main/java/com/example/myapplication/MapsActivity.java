package com.example.myapplication;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        AttractionDatabaseHelper dbHelper = new AttractionDatabaseHelper(this);

        List<Attraction> attractions = dbHelper.getAllAttractions();
        Log.d("MapsActivity", "Adatbázisból betöltve: " + attractions.size() + " elem.");


        for (Attraction attr : attractions) {
            LatLng location = new LatLng(attr.getLatitude(), attr.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(attr.getName())
                    .snippet(attr.getCity()));
            Log.d("MapsActivity", "Marker hozzáadva: " + attr.getName());
        }

        if (!attractions.isEmpty()) {
            LatLng firstLocation = new LatLng(attractions.get(0).getLatitude(), attractions.get(0).getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f));
        } else {
            LatLng targuMures = new LatLng(46.545, 24.5625);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targuMures, 9f));
        }
    }
}