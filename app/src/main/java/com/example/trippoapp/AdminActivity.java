package com.example.trippoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class AdminActivity extends AppCompatActivity {

    Button season, recommend, logout;
    FrameLayout fragmnetContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        season = findViewById(R.id.button1);
        recommend = findViewById(R.id.button2);
        fragmnetContainer = findViewById(R.id.fragment_container1);
        logout = findViewById(R.id.logout);

        SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);
        String val = sp.getString("id1","");

        if (val.isEmpty()){
            Intent intent = new Intent(this, SigninActivity.class);
            startActivity(intent);
        }

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
        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container1, new AdminRecommendFragment());
                fragmentTransaction.commit();

                season.setVisibility(View.GONE);
                recommend.setVisibility(View.GONE);
                logout.setVisibility(View.GONE);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("id1");
                editor.apply();

                Intent intent = new Intent(AdminActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}