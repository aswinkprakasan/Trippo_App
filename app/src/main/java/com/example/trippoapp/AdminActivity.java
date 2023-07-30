package com.example.trippoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class AdminActivity extends AppCompatActivity {

    Button season, recommend;
    FrameLayout fragmnetContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        season = findViewById(R.id.button1);
        recommend = findViewById(R.id.button2);
        fragmnetContainer = findViewById(R.id.fragment_container1);

        season.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container1, new AdminSeasonFragment());
                fragmentTransaction.commit();

                season.setVisibility(View.GONE);
                recommend.setVisibility(View.GONE);

            }
        });
    }
}