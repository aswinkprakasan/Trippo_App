package com.example.trippoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GetStartedActivity extends AppCompatActivity {

    Button startBtn;
    TextView skipBtn;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        startBtn = findViewById(R.id.startButton);
        skipBtn = findViewById(R.id.skip_btn);

        sp = getSharedPreferences("MyPref", MODE_PRIVATE);
        boolean firstTime = sp.getBoolean("FirstTime", true);

        if (firstTime){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("FirstTime", false);
            editor.apply();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}