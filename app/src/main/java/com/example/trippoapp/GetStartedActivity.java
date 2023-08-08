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
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        startBtn = findViewById(R.id.startButton);

        sp = getSharedPreferences("MyPref", MODE_PRIVATE);
        boolean firstTime = sp.getBoolean("FirstTime", true);

        if (firstTime){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("FirstTime", false);
            editor.apply();
        }
        else {
            String val1 = sp.getString("id","");
            String val2 = sp.getString("id1","");

            if (!val1.isEmpty()){
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            } else if (!val2.isEmpty()) {

                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(intent);
                finish();
            }

        }


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}