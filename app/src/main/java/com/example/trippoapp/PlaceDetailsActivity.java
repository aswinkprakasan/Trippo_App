package com.example.trippoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class PlaceDetailsActivity extends AppCompatActivity {

    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        name = findViewById(R.id.name);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            name.setText(bundle.getString("Id"));
        }

    }
}