package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button btnHistorical = findViewById(R.id.btn_historical);
        Button btnNatural = findViewById(R.id.btn_natural);
        Button btnShowAll = findViewById(R.id.btn_show_all);

        btnHistorical.setOnClickListener(v -> startListActivity("Történelmi helyszín"));
        btnNatural.setOnClickListener(v -> startListActivity("Természeti csoda"));
        btnShowAll.setOnClickListener(v -> startListActivity("Mind mutatása"));
    }

    private void startListActivity(String filter) {
        Intent intent = new Intent(MainActivity.this, AttractionListActivity.class);
        intent.putExtra("INITIAL_FILTER", filter);
        startActivity(intent);
    }
}