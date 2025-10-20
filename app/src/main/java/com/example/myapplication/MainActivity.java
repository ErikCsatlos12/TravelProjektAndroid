package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> attractionsDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.attractions_recycler_view);

        attractionsDataList = new ArrayList<>();

        attractionsDataList.add(new HistoricalSite("Marosvásárhelyi vár", "Marosvásárhely", 4.6, 1492));
        attractionsDataList.add(new NaturalWonder("Medve-tó", "Szováta", 4.6, "sós vizű tó"));
        attractionsDataList.add(new HistoricalSite("Teleki Téka", "Marosvásárhely", 4.7, 1802));
        attractionsDataList.add(new NaturalWonder("Bozodi-tó", "Erdőszentgyörgy", 4.7, "víztározó"));
        attractionsDataList.add(new HistoricalSite("Segesvári vár", "Segesvár", 4.8, 1350));
        attractionsDataList.add(new NaturalWonder("Gyilkos-tó", "Gyergyószentmiklós", 4.7, "torlasztó"));

        adapter = new AttractionAdapter(attractionsDataList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);
    }
}





