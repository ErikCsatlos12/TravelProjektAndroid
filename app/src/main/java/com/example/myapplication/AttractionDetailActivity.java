package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AttractionDetailActivity extends BaseActivity implements OnMapReadyCallback {

    private double targetLatitude;
    private double targetLongitude;
    private String attractionName;
    private int imageResIdToShow = 0;

    private GoogleMap miniMap;
    private FrameLayout mapContainer;
    private ArrayList<Double> routeLats;
    private ArrayList<Double> routeLngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);

        ImageView detailImage = findViewById(R.id.detail_image);
        TextView detailName = findViewById(R.id.detail_name);
        TextView detailRating = findViewById(R.id.detail_rating);
        TextView detailCategory = findViewById(R.id.detail_category);
        TextView detailDescription = findViewById(R.id.detail_description);
        Button mapButton = findViewById(R.id.detail_map_button);
        FloatingActionButton fabHome = findViewById(R.id.fab_home);
        ImageButton infoPriceButton = findViewById(R.id.info_price_button);
        mapContainer = findViewById(R.id.map_container);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            attractionName = extras.getString("NAME");
            String description = extras.getString("DESCRIPTION");
            String imageName = extras.getString("IMAGE_NAME");
            String category = extras.getString("CATEGORY");
            double rating = extras.getDouble("RATING");
            targetLatitude = extras.getDouble("LATITUDE");
            targetLongitude = extras.getDouble("LONGITUDE");

            routeLats = (ArrayList<Double>) extras.getSerializable("ROUTE_LATS");
            routeLngs = (ArrayList<Double>) extras.getSerializable("ROUTE_LNGS");

            detailName.setText(attractionName);
            detailDescription.setText(description);
            detailRating.setText(String.format("%.1f", rating));
            detailCategory.setText(category);

            int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            if (imageResId != 0) {
                imageResIdToShow = imageResId;
                detailImage.setImageResource(imageResIdToShow);
            } else {
                imageResIdToShow = R.drawable.ic_launcher_foreground;
                detailImage.setImageResource(imageResIdToShow);
            }

            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "google.navigation:q=" + targetLatitude + "," + targetLongitude;
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });

            if (routeLats != null && routeLngs != null) {
                // Ha ez egy Séta, jelenítsük meg a mini-térképet...
                mapContainer.setVisibility(View.VISIBLE);
                // ...és REJTSÜK EL a fő "Navigáció" gombot.
                mapButton.setVisibility(View.GONE);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.detail_map_fragment);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(this);
                }
            } else {
                // Ha ez nem Séta, rejtsük el a mini-térképet...
                mapContainer.setVisibility(View.GONE);
                // ...és MUTASSUK a fő "Navigáció" gombot.
                mapButton.setVisibility(View.VISIBLE);
            }
        }

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        infoPriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriceInfoDialog();
            }
        });

        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        miniMap = googleMap;
        // JAVÍTVA: Engedélyezzük a görgetést/mozgatást
        miniMap.getUiSettings().setScrollGesturesEnabled(true);

        List<LatLng> routePoints = new ArrayList<>();
        for (int i = 0; i < routeLats.size(); i++) {
            routePoints.add(new LatLng(routeLats.get(i), routeLngs.get(i)));
        }

        if (routePoints.isEmpty()) return;

        PolylineOptions polylineOptions = new PolylineOptions()
                .color(0xFFFF5722)
                .width(10);

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (LatLng point : routePoints) {
            polylineOptions.add(point);
            boundsBuilder.include(point);
        }

        miniMap.addPolyline(polylineOptions);

        LatLng startPoint = routePoints.get(0);
        miniMap.addMarker(new MarkerOptions().position(startPoint).title(attractionName));

        LatLngBounds bounds = boundsBuilder.build();
        miniMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void showPriceInfoDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.price_info_dialog_title))
                .setMessage(getString(R.string.price_info_dialog_message))
                .setPositiveButton(getString(R.string.dialog_button_ok), new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showImageDialog() {
        if (imageResIdToShow == 0) return;

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fullscreen_image);
        ImageView fullscreenImage = dialog.findViewById(R.id.fullscreen_image_view);
        fullscreenImage.setImageResource(imageResIdToShow);

        fullscreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        dialog.show();
    }
}