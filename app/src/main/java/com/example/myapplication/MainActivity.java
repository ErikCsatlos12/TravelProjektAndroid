package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends BaseActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 102;
    private static final String TAG = "MainActivity_Token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button btnHistorical = findViewById(R.id.btn_historical);
        Button btnNatural = findViewById(R.id.btn_natural);
        Button btnAdventure = findViewById(R.id.btn_adventure);
        Button btnShowAll = findViewById(R.id.btn_show_all);

        FloatingActionButton btnChangeLanguage = findViewById(R.id.btn_change_language);

        btnHistorical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity(getString(R.string.category_historical));
            }
        });

        btnNatural.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity(getString(R.string.category_natural));
            }
        });

        btnAdventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity(getString(R.string.category_adventure));
            }
        });

        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity("Mind mutatása");
            }
        });

        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageChangeDialog();
            }
        });

        checkNotificationPermission();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Hiba a Firebase Token lekérésekor", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        Log.d(TAG, "JELENLEGI FIREBASE TOKEN: " + token);
                    }
                });

    }

    private void showLanguageChangeDialog() {
        final String[] languages = {"Magyar", "English", "Română"};
        final String[] languageCodes = {"hu", "en", "ro"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.change_language_button));

        builder.setItems(languages, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                LocaleHelper.setLocale(MainActivity.this, languageCodes[which]);

                recreate();
            }
        });

        builder.create().show();
    }

    private void startListActivity(String filter) {
        Intent intent = new Intent(MainActivity.this, AttractionListActivity.class);
        intent.putExtra("INITIAL_FILTER", filter);
        startActivity(intent);
    }


    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
                Log.d(TAG, "Értesítési engedély megadva.");
            } else {
                Log.d(TAG, "Értesítési engedély elutasítva.");
            }
        }
    }
}