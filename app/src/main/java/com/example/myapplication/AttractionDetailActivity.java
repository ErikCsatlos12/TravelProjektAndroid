package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AttractionDetailActivity extends BaseActivity implements OnMapReadyCallback {

    private String attractionId;
    private Attraction currentAttraction;

    private GoogleMap miniMap;
    private FrameLayout mapContainer;
    private List<LatLng> decodedRoutePoints;

    private TextView detailName, detailRating, detailCategory, detailDescription;
    private ImageView detailImage;
    private Button mapButton;
    private ImageButton infoPriceButton;
    private FloatingActionButton fabHome;

    private int imageResIdToShow = 0;

    private FirebaseFirestore db;
    private DirectionsApiService apiService;
    private String googleMapsApiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);

        db = FirebaseFirestore.getInstance();
        googleMapsApiKey = getString(R.string.google_maps_key);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(DirectionsApiService.class);

        detailImage = findViewById(R.id.detail_image);
        detailName = findViewById(R.id.detail_name);
        detailRating = findViewById(R.id.detail_rating);
        detailCategory = findViewById(R.id.detail_category);
        detailDescription = findViewById(R.id.detail_description);
        mapButton = findViewById(R.id.detail_map_button);
        fabHome = findViewById(R.id.fab_home);
        infoPriceButton = findViewById(R.id.info_price_button);
        mapContainer = findViewById(R.id.map_container);

        attractionId = getIntent().getStringExtra("ATTRACTION_ID");

        if (attractionId != null) {
            loadAttractionData();
        } else {
            Toast.makeText(this, "Hiba: Látványosság nem található.", Toast.LENGTH_SHORT).show();
            finish();
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

    private void loadAttractionData() {
        DocumentReference docRef = db.collection("attractions").document(attractionId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Toast.makeText(this, "Hiba: Adatbázis hiba.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String category = documentSnapshot.getString("category");
            if ("Historical".equals(category) || "Cultural".equals(category)) {
                currentAttraction = documentSnapshot.toObject(HistoricalSite.class);
            } else if ("Natural".equals(category)) {
                currentAttraction = documentSnapshot.toObject(NaturalWonder.class);
            } else if ("Adventure".equals(category)) {
                currentAttraction = documentSnapshot.toObject(AdventureSite.class);
            } else if ("Walk".equals(category)) {
                currentAttraction = documentSnapshot.toObject(Seta.class);
            }

            if (currentAttraction != null) {
                currentAttraction.setDocumentId(documentSnapshot.getId());
                populateUI();
            }
        });
    }

    private void populateUI() {
        detailName.setText(currentAttraction.getLocalizedName(this));
        detailDescription.setText(currentAttraction.getLocalizedDescription(this));
        detailRating.setText(String.format(Locale.US, "%.1f", currentAttraction.getRating()));
        detailCategory.setText(currentAttraction.getCategory(this));

        int imageResId = getResources().getIdentifier(currentAttraction.getImageName(), "drawable", getPackageName());
        if (imageResId != 0) {
            imageResIdToShow = imageResId;
        } else {
            imageResIdToShow = R.drawable.ic_launcher_foreground;
        }
        detailImage.setImageResource(imageResIdToShow);

        if (currentAttraction instanceof Seta) {
            mapContainer.setVisibility(View.VISIBLE);
            mapButton.setVisibility(View.VISIBLE);
            mapButton.setText(getString(R.string.detail_walk_button));
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startWalkNavigation();
                }
            });
            fetchRouteAndInitMap();
        } else {
            mapContainer.setVisibility(View.GONE);
            mapButton.setVisibility(View.VISIBLE);
            mapButton.setText(getString(R.string.detail_nav_button));
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPointNavigation();
                }
            });
        }

        if (currentAttraction instanceof Dijkoteles) {
            if (((Dijkoteles) currentAttraction).getAr() > 0) {
                infoPriceButton.setVisibility(View.VISIBLE);
            } else {
                infoPriceButton.setVisibility(View.GONE);
            }
        } else {
            infoPriceButton.setVisibility(View.GONE);
        }
    }

    private void startPointNavigation() {
        String uri = "google.navigation:q=" + currentAttraction.getLatitude() + "," + currentAttraction.getLongitude();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void startWalkNavigation() {
        Seta seta = (Seta) currentAttraction;
        List<GeoPoint> waypoints = seta.getWaypoints();

        if (waypoints == null || waypoints.isEmpty()) {
            Toast.makeText(this, "Hiba: Ehhez a sétához nincsenek útvonalpontok.", Toast.LENGTH_SHORT).show();
            return;
        }

        String origin = currentAttraction.getLatitude() + "," + currentAttraction.getLongitude();
        GeoPoint endPoint = waypoints.get(waypoints.size() - 1);
        String destination = endPoint.getLatitude() + "," + endPoint.getLongitude();

        StringBuilder waypointString = new StringBuilder();
        if (waypoints.size() > 1) {
            for (int i = 0; i < waypoints.size() - 1; i++) {
                GeoPoint point = waypoints.get(i);
                waypointString.append(point.getLatitude()).append(",").append(point.getLongitude());
                if (i < waypoints.size() - 2) {
                    waypointString.append("|");
                }
            }
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.google.com")
                .appendPath("maps")
                .appendQueryParameter("saddr", origin)
                .appendQueryParameter("daddr", destination)
                .appendQueryParameter("travelmode", "walking");

        if (waypointString.length() > 0) {
            builder.appendQueryParameter("waypoints", waypointString.toString());
        }

        String url = builder.build().toString();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.toast_no_app_for_route), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchRouteAndInitMap() {
        Seta seta = (Seta) currentAttraction;
        List<GeoPoint> waypoints = seta.getWaypoints();

        if (waypoints == null || waypoints.isEmpty()) {
            mapContainer.setVisibility(View.GONE);
            return;
        }

        String origin = currentAttraction.getLatitude() + "," + currentAttraction.getLongitude();
        String destination;
        String waypointString = "";

        if (waypoints.size() == 1) {
            GeoPoint endPoint = waypoints.get(0);
            destination = endPoint.getLatitude() + "," + endPoint.getLongitude();
        } else {
            GeoPoint endPoint = waypoints.get(waypoints.size() - 1);
            destination = endPoint.getLatitude() + "," + endPoint.getLongitude();

            StringBuilder wb = new StringBuilder();
            for (int i = 0; i < waypoints.size() - 1; i++) {
                GeoPoint point = waypoints.get(i);
                wb.append(point.getLatitude()).append(",").append(point.getLongitude());
                if (i < waypoints.size() - 2) {
                    wb.append("|");
                }
            }
            waypointString = wb.toString();
        }

        apiService.getDirections(origin, destination, waypointString, "walking", googleMapsApiKey)
                .enqueue(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().getRoutes().isEmpty()) {
                            String encodedPolyline = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                            decodedRoutePoints = PolyUtil.decode(encodedPolyline);

                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.detail_map_fragment);
                            if (mapFragment != null) {
                                mapFragment.getMapAsync(AttractionDetailActivity.this);
                            }
                        } else {
                            mapContainer.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e("DirectionsAPI", "Hálózati hiba", t);
                        mapContainer.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        miniMap = googleMap;
        miniMap.getUiSettings().setScrollGesturesEnabled(true);

        if (decodedRoutePoints == null || decodedRoutePoints.isEmpty()) return;

        PolylineOptions polylineOptions = new PolylineOptions()
                .color(0xFFFF5722)
                .width(10);

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (LatLng point : decodedRoutePoints) {
            polylineOptions.add(point);
            boundsBuilder.include(point);
        }

        miniMap.addPolyline(polylineOptions);

        LatLng startPoint = decodedRoutePoints.get(0);
        LatLng endPoint = decodedRoutePoints.get(decodedRoutePoints.size() - 1);

        miniMap.addMarker(new MarkerOptions().position(startPoint).title(currentAttraction.getLocalizedName(this)));
        miniMap.addMarker(new MarkerOptions().position(endPoint).title("Végpont"));

        LatLngBounds bounds = boundsBuilder.build();
        miniMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void showPriceInfoDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.price_info_dialog_title))
                .setMessage(getString(R.string.price_info_dialog_message))
                .setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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