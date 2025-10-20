package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
   ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Attraction d1 = new Attraction("Marosvásárhelyi vár", "Marosvásárhely", 0, 4.6);
        TextView attractionTextView = findViewById(R.id.attraction_name_textview);
        attractionTextView.setText(d1.getName());



    }
}





