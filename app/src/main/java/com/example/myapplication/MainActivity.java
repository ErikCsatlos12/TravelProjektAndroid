package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 102; // Új kód az értesítéshez

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button btnHistorical = findViewById(R.id.btn_historical);
        Button btnNatural = findViewById(R.id.btn_natural);
        Button btnAdventure = findViewById(R.id.btn_adventure);
        Button btnShowAll = findViewById(R.id.btn_show_all);

        btnHistorical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity("Történelmi helyszín");
            }
        });

        btnNatural.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity("Természeti csoda");
            }
        });

        btnAdventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity("Adventure");
            }
        });

        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity("Mind mutatása");
            }
        });

        checkNotificationPermission();
    }

    private void startListActivity(String filter) {
        Intent intent = new Intent(MainActivity.this, AttractionListActivity.class);
        intent.putExtra("INITIAL_FILTER", filter);
        startActivity(intent);
    }


    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU = Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Notification", "Értesítési engedély megadva.");
            } else {
                Log.d("Notification", "Értesítési engedély elutasítva.");
            }
        }
    }
}