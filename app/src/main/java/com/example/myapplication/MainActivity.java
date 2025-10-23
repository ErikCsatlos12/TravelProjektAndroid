package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> attractionsDataList;


    private AttractionDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.attractions_recycler_view);


        try {

            dbHelper = new AttractionDatabaseHelper(this);

            attractionsDataList = dbHelper.getAllAttractions();


            if (attractionsDataList.isEmpty()) {
                Log.d("MainActivity", "Adatbázis üres, feltöltés indítása...");

                populateDatabase();

                attractionsDataList = dbHelper.getAllAttractions();
                Log.d("MainActivity", "Feltöltés kész, " + attractionsDataList.size() + " elem betöltve.");
            }

            adapter = new AttractionAdapter(attractionsDataList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {

            Log.e("MainActivity", "Hiba történt az adatbázis kezelésekor!", e);
            e.printStackTrace();
        }
        // ----- TRY-CATCH BLOKK VÉGE -----
    }


    private void populateDatabase() {
        dbHelper.addAttraction(new HistoricalSite("Marosvásárhelyi vár", "Marosvásárhely", 4.6, 1492, 0.0));
        dbHelper.addAttraction(new NaturalWonder("Medve-tó", "Szováta", 4.6, "sós vizű tó", 30.0));
        dbHelper.addAttraction(new HistoricalSite("Teleki Téka", "Marosvásárhely", 4.7, 1802, 15.0));
        dbHelper.addAttraction(new NaturalWonder("Bozodi-tó", "Erdőszentgyörgy", 4.7, "víztározó", 0.0));
        dbHelper.addAttraction(new HistoricalSite("Segesvári vár", "Segesvár", 4.8, 1350, 25.0));
        dbHelper.addAttraction(new NaturalWonder("Gyilkos-tó", "Gyergyószentmiklós", 4.7, "torlasztó", 0.0));
    }
}