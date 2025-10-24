package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.net.Uri;

public class AttractionDetailActivity extends AppCompatActivity {

    private double targetLatitude;
    private double targetLongitude;
    private String attractionName;

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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            attractionName = extras.getString("NAME");
            String description = extras.getString("DESCRIPTION");
            String imageName = extras.getString("IMAGE_NAME");
            String category = extras.getString("CATEGORY");

            double rating = extras.getDouble("RATING");
            targetLatitude = extras.getDouble("LATITUDE"); // Globális változóba mentjük
            targetLongitude = extras.getDouble("LONGITUDE"); // Globális változóba mentjük

            detailName.setText(attractionName);
            detailDescription.setText(description);
            detailRating.setText(String.format("%.1f", rating));
            detailCategory.setText(category);

            int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            if (imageResId != 0) {
                detailImage.setImageResource(imageResId);
            } else {
                detailImage.setImageResource(R.drawable.ic_launcher_foreground);
            }

            mapButton.setOnClickListener(v -> {
                String uri = "google.navigation:q=" + targetLatitude + "," + targetLongitude;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps");

                startActivity(mapIntent);
            });
        }
    }
}